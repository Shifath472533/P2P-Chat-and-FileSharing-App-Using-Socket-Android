package com.example.manug.peerchat;

import android.content.Intent;
import android.os.AsyncTask;
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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public final static int SOCKET_PORT = 13267;
    public final static String FILE_TO_SEND = "G:/Elements.of.Programming.pdf";

    String ipAddress,portNo;
    //public static String message="";
    EditText messageTextView;
    TextView responseTextView;
    static MessageAdapter mAdapter;
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
                    String path = data.getData().getPath();
                    messageTextView.setText(path);
                }
        }
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
        String msg = messageTextView.getText().toString();;
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String ipadd = ipAddress;
                int portr = Integer.parseInt(portNo);
                Socket clientSocket = new Socket(ipadd, portr);
                OutputStream outToServer =clientSocket.getOutputStream();
                PrintWriter output = new PrintWriter(outToServer);
                output.println(msg);
                output.flush();
                clientSocket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return msg;
        }
        protected void onPostExecute(String result) {
            messageArray.add(new Message("Sent: " + result, 0));
            message_List.setAdapter(mAdapter);
            messageTextView.setText("");
        }
    }

    public class FileClient extends AsyncTask<Void,Void,String>{
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        ServerSocket servsock = null;
        Socket sock = null;
        @Override
        protected String doInBackground(Void... voids) {
            String fileName = "Programming.pdf";
            try {
                int portr = Integer.parseInt(portNo);
                servsock = new ServerSocket(portr);
                while (true) {
                    //System.out.println("Waiting...");
                    Toast toast=Toast.makeText(getApplicationContext(),"Waiting...",Toast.LENGTH_LONG);
                    toast.setMargin(50,50);
                    toast.show();
                    try {
                        sock = servsock.accept();
                        toast=Toast.makeText(getApplicationContext(),"Accepted connection : " + sock,Toast.LENGTH_LONG);
                        toast.setMargin(50,50);
                        toast.show();
                        //System.out.println("Accepted connection : " + sock);
                        // send file
                        File myFile = new File (FILE_TO_SEND);
                        byte [] mybytearray  = new byte [(int)myFile.length()];
                        fis = new FileInputStream(myFile);
                        bis = new BufferedInputStream(fis);
                        bis.read(mybytearray,0,mybytearray.length);
                        os = sock.getOutputStream();
                        System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
                        os.write(mybytearray,0,mybytearray.length);
                        os.flush();
                        System.out.println("Done.");
                    }
                    finally {
                        if (bis != null) bis.close();
                        if (os != null) os.close();
                        if (sock!=null) sock.close();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (servsock != null) {
                    try {
                        servsock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return fileName;
        }
        protected void onPostExecute(String result) {
            messageArray.add(new Message("Sent: " + result, 0));
            message_List.setAdapter(mAdapter);

        }
    }
}
