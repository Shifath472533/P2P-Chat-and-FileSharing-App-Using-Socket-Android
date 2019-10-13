package com.example.manug.peerchat;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public String FILE_TO_SEND;

    String ipAddress,portNo;
    //public static String message="";
    EditText messageTextView;
    TextView responseTextView;
    static MessageAdapter mAdapter;
    int fileSelected = 0;
    ListView message_List;
    ArrayList<Message> messageArray;
    EditText portText;
    int myport;
    Intent fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            Log.d("STATE",savedInstanceState.toString());
        }
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String info = bundle.getString("ip&port");
            String[] infos = info.split(" ");
            ipAddress = infos[0];
            portNo = infos[1];
            myport = Integer.parseInt(infos[2]);
            Log.d("info",ipAddress+" "+portNo+" "+myport);
        }

        message_List = findViewById(R.id.message_list);
        messageArray = new ArrayList<Message>();
        mAdapter = new MessageAdapter(this, messageArray);
        message_List.setAdapter(mAdapter);
        messageTextView= (EditText) findViewById(R.id.messageEditText);
        //message = messageTextView.getText().toString();
        Server s = new Server(message_List, messageArray, myport);
        s.start();
    }

    public void selectFileResponse(View view){
        fileManager = new Intent(Intent.ACTION_GET_CONTENT);
        fileManager.setType("*/*");
        startActivityForResult(fileManager,10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 10:
                if(resultCode==RESULT_OK){
                    Uri uri = data.getData();
                    String path = getFilePathFromUri(uri);
                    fileSelected = 1;
                    messageTextView.setText(path);
                    FILE_TO_SEND = path;
                    Log.d("problem", "onActivityResult: "+FILE_TO_SEND);
                }
        }
    }

    private String getFilePathFromUri(Uri uri){
        String path = uri.getPathSegments().get(1);
        path = Environment.getExternalStorageDirectory().getPath()+"/"+path.split(":")[1];
        return path;
    }

    public void sendResponse(View view){
        Client c =new Client();
        c.execute();
    }
    public void setView(String s){
        String str=responseTextView.getText().toString();
        str=str+"\nReceived: "+s;
        responseTextView.setText(str);
    }
    public class Client extends AsyncTask<Void,Void,String> {
        String msg = messageTextView.getText().toString();
        String path = FILE_TO_SEND;
        public int isFile = fileSelected;

        @Override
        protected String doInBackground(Void... voids) {
            Log.d("problem", "fileSelected = "+fileSelected);
            try {
                if(fileSelected == 0)
                {
                    String ipadd = ipAddress;
                    //Log.d("problem", "ip add");

                    int portr = Integer.parseInt(portNo);
                    //Log.d("problem", "port");

                    //Log.d("problem", "ip add "+ipadd+" "+portr);
                    Socket clientSocket = new Socket(ipadd, portr);
                    //Log.d("problem", "socket");
                    Message message = new Message(msg, 0);

                    Log.d("problem", "fileSelected = "+fileSelected);

                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(message);
                    out.flush();
                    clientSocket.close();




////                    String ipadd = ipAddress;
////                    int portr = Integer.parseInt(portNo);
////                    Socket clientSocket = new Socket(ipadd, portr);
////                    OutputStream outToServer =clientSocket.getOutputStream();
////                    PrintWriter output = new PrintWriter(outToServer);
////                    output.println(msg);
////                    output.flush();
////                    clientSocket.close();
                }
                else if(fileSelected == 1)
                {
                    FileInputStream fis = null;
                    BufferedInputStream bis = null;

                    String ipadd = ipAddress;
                    int portr = Integer.parseInt(portNo);
                    Socket clientSocket = new Socket(ipadd, portr);

                    Log.d("problem", "doInBackground: "+path);
                    File myfile = new File(path);
                    Log.d("problem", "doInBackground2222: "+myfile+"   "+FILE_TO_SEND);

                    if(myfile.exists())
                        //'ll add a toast here!!!
                        Log.d("problem", "Exist:   Selected file exists");
                    else
                        //Here also !!!
                        Log.d("problem", "Exist:   Selected file doesn't exists");

                    byte [] mybytearray  = new byte [(int)myfile.length()];
                    Log.d("byte", "doInBackground: "+mybytearray.length);
//                    for(int i=0;i<mybytearray.length;i++){
//                        Log.d("byte", "doInBackground: "+mybytearray[i]+" ");
//                    }
                    fis = new FileInputStream(myfile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);

                    Message message = new Message(msg, mybytearray, 0);

                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(message);
                    out.flush();
                    clientSocket.close();

                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return msg;
        }
        protected void onPostExecute(String result) {
            messageArray.add(new Message("Sent: " + result, 0));
            fileSelected=0;
            Log.d("problem", "onPostExecute: "+result);
            message_List.setAdapter(mAdapter);
            messageTextView.setText("");
        }
    }

}
