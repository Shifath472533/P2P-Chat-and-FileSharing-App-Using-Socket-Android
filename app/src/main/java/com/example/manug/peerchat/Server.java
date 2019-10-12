package com.example.manug.peerchat;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import static com.example.manug.peerchat.ChatActivity.mAdapter;
public class Server extends Thread {
    ListView messageList;
    ArrayList<Message> messageArray;
    int port;
    public Server(ListView messageList, ArrayList<Message> messageArray, int port) {
        this.messageArray = messageArray;
        this.messageList = messageList;
        this.port = port;
    }
    ServerSocket welcomeSocket=null;
    @Override
    public void run(){
        try{
            String sentence;
            welcomeSocket=new ServerSocket(port);
            while (true){
                Socket connectionSocket=welcomeSocket.accept();
                HandleClient c= new HandleClient();
                c.execute(connectionSocket);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public class HandleClient extends AsyncTask<Socket,Void,String>{
        String sentence;
        @Override
        protected String doInBackground(Socket... sockets) {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(sockets[0].getInputStream()));
                sentence = input.readLine();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return sentence ;
        }
        protected void onPostExecute(String result) {

//            Log.d("problem", "onPostExecute:" + result);
            messageArray.add(new Message("Received: " + result, 1));

//            for(Message mssg: messageArray){
//                String sst = mssg.getMessage();
//                Log.d("problem","received             "+sst);
//            }

            messageList.setAdapter(mAdapter);
            Log.d("problem","Received: " + result);
            for(Message mssg: messageArray){
                String sst = mssg.getMessage();
                //Log.d("problem","              "+sst);
            }
        }
    }
}
