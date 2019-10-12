package com.example.manug.peerchat;

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
import java.util.Arrays;

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

        message_List = (ListView) findViewById(R.id.message_list);
        messageArray = new ArrayList<Message>();
        mAdapter = new MessageAdapter(this, messageArray);
        message_List.setAdapter(mAdapter);
        messageTextView= (EditText) findViewById(R.id.messageEditText);
        //message = messageTextView.getText().toString();
        startServer();
    }

    void startServer(){
        Server s = new Server(message_List, messageArray, myport);
        s.start();
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

            Log.d("problem","Sent: " + result);
            for(Message mssg: messageArray){
                String sst = mssg.getMessage();
                //Log.d("problem","              "+sst);
            }

            messageTextView.setText("");
        }
    }

}
