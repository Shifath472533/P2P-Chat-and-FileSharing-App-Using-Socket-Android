package com.example.manug.peerchat;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import static com.example.manug.peerchat.ChatActivity.mAdapter;
public class Server extends Thread {
    String bgColorCode;
    ChatActivity activity;
    ListView messageList;
    int bgselected = 0;
    ArrayList<Message> messageArray;
    public final static int FILE_SIZE = 6022386;
    public static String FILE_TO_RECEIVE ="";
    int port;
    public Server(ListView messageList, ArrayList<Message> messageArray, int port, ChatActivity activity) {
        this.messageArray = messageArray;
        this.messageList = messageList;
        this.port = port;
        this.activity = activity;
    }
    ServerSocket welcomeSocket=null;

    public String getBgColorCode() {
        return bgColorCode;
    }

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
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        @Override
        protected String doInBackground(Socket... sockets) {
            try {

                 ObjectInputStream in = new ObjectInputStream(sockets[0].getInputStream());
                 Message message = (Message) in.readObject();

                 sentence = message.getMessage();
                 Log.d("problem","R: " + sentence);
                 if(message.isFile()){
                     FILE_TO_RECEIVE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"P2P-Chat-And-File-Sharing-App";
                     Log.d("problem", "doInBackground: "+FILE_TO_RECEIVE);
                     File directory = new File(FILE_TO_RECEIVE);
                     if(!directory.exists()){
                         directory.mkdirs();
                     }
                     FILE_TO_RECEIVE+=File.separator+message.getMessage();
                     byte [] mybytearray  = message.getMybytearray();
                     fos = new FileOutputStream(FILE_TO_RECEIVE);
                     bos = new BufferedOutputStream(fos);
                     bos.write(mybytearray);
                     bos.flush();
                 }

                 if(message.isBackground()){
                     bgColorCode = message.getMessage();
                     bgselected = 1;
                     activity.setMessage(message);
                 }



//                BufferedReader input = new BufferedReader(new InputStreamReader(sockets[0].getInputStream()));
//                sentence = input.readLine();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return sentence ;
        }
        protected void onPostExecute(String result) {

//            Log.d("problem", "onPostExecute:" + result);

            if(bgselected == 1){
                bgselected = 0;
            }
            else {
                messageArray.add(new Message("Received: " + result, 1));

//            for(Message mssg: messageArray){
//                String sst = mssg.getMessage();
//                Log.d("problem","received             "+sst);
//            }

                messageList.setAdapter(mAdapter);
                messageList.setSelection(messageList.getCount() - 1);

                Log.d("problem", "Received: " + result);
                for (Message mssg : messageArray) {
                    String sst = mssg.getMessage();
                    //Log.d("problem","              "+sst);
                }
            }
        }
    }
}
