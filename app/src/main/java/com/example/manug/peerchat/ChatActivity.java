package com.example.manug.peerchat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public String FILE_TO_SEND;
    LinearLayout chatActivityLayout;
    String ipAddress,portNo;
    //public static String message="";
    EditText messageTextView;
    TextView responseTextView;
    static MessageAdapter mAdapter;
    int fileSelected = 0;
    int bgselected = 0;
    ListView message_List;
    ArrayList<Message> messageArray;
    EditText portText;
    int myport;
    Intent fileManager;
    int BGID = 0;
    String bgColorCode;


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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        message_List = findViewById(R.id.message_list);
        messageArray = new ArrayList<Message>();
        mAdapter = new MessageAdapter(this, messageArray);
        message_List.setAdapter(mAdapter);
        messageTextView= (EditText) findViewById(R.id.messageEditText);
        //message = messageTextView.getText().toString();
        chatActivityLayout = findViewById(R.id.chatActivityView);

        message_List.setBackgroundResource(R.drawable.background);


        Server s = new Server(message_List, messageArray, myport, this);
        s.start();
    }


    //for menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuinflater = getMenuInflater();
        menuinflater.inflate(R.menu.menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.changeBackgroundid) {

             openBackgroundAlertDialog ();
        }
        if(item.getItemId() == R.id.saveMessageid){

            Toast.makeText(getApplicationContext(), "Save Message Selected", Toast.LENGTH_SHORT).show();
            saveMessage();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openBackgroundAlertDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.change_background_dialog, null);

        Button btn_cancel = (Button) mView.findViewById(R.id.btn_cancel);
        Button btn1= (Button) mView.findViewById(R.id.btn1);
        Button btn2= (Button) mView.findViewById(R.id.btn2);
        Button btn3= (Button) mView.findViewById(R.id.btn3);
        Button btn4= (Button) mView.findViewById(R.id.btn4);


        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle("Disconnecting");

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ChatActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                bgselected = 1;
                alertDialog.dismiss();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Background Changed", Toast.LENGTH_SHORT).show();
                BGID = 1;
                bgselected = 1;
                message_List.setBackgroundResource(R.drawable.background1);
                changeBG();
                alertDialog.dismiss();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Background Changed", Toast.LENGTH_SHORT).show();
                bgselected = 1;
                BGID = 2;
                message_List.setBackgroundResource(R.drawable.background2);
                changeBG();
                alertDialog.dismiss();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Background Changed", Toast.LENGTH_SHORT).show();
                bgselected = 1;
                BGID = 3;
                message_List.setBackgroundResource(R.drawable.background3);
                changeBG();
                alertDialog.dismiss();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgselected = 1;
                BGID = 4;
                message_List.setBackgroundResource(R.drawable.background4);
                changeBG();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

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
                    Log.d("problem", "onActivityResult: "+uri);
                    //messageTextView.setText(uri.toString());
                    String path = getFilePathFromUri(uri);
                    //String path = getRealPathFromURI(this,uri);
                    fileSelected = 1;
                    messageTextView.setText(path);
                    FILE_TO_SEND = path;
                    Log.d("problem", "onActivityResult: "+FILE_TO_SEND);
                }
        }
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("problem", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
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

    public void changeBG(){
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
                if(bgselected == 1) {

                    if(BGID == 1){
                        msg = "@@bg1";
                    }
                    if(BGID == 2){
                        msg = "@@bg2";
                    }
                    if(BGID == 3){
                        msg = "@@bg3";
                    }
                    if(BGID == 4){
                        msg = "@@bg4";
                    }

                    String ipadd = ipAddress;
                    //Log.d("problem", "ip add");

                    int portr = Integer.parseInt(portNo);
                    //Log.d("problem", "port");

                    //Log.d("problem", "ip add "+ipadd+" "+portr);
                    Socket clientSocket = new Socket(ipadd, portr);
                    //Log.d("problem", "socket");
                    Message message = new Message(msg, 0);
                    message.setBG();

                    Log.d("problem", "fileSelected = "+fileSelected);

                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(message);
                    out.flush();
                    clientSocket.close();

                }
                else if(fileSelected == 0)
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
                    String filename = path.substring(path.lastIndexOf("/")+1);
                    msg = filename;
                    Log.d("problem", "Real Path: " + path);
                    Log.d("problem", "Filename With Extension: " + filename);

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
            if(bgselected == 1){
                bgselected = 0;
            }
            else {
                messageArray.add(new Message("Sent: " + result, 0));
                fileSelected = 0;
                Log.d("problem", "onPostExecute: " + result);
                message_List.setAdapter(mAdapter);
                message_List.setSelection(message_List.getCount() - 1);
                messageTextView.setText("");
            }
        }
    }


    void setMessage(final Message msg)
    {

                Log.d("background", "message = "+msg.getMessage());

        if(msg.getMessage().equals("@@bg1")) {
            Log.d("BACKGROUND", "setMessage: ");
            //message_List.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            message_List.setBackgroundResource(R.drawable.background1);
        }
        else if(msg.getMessage().equals("@@bg2")) {
            message_List.setBackgroundResource(R.drawable.background2);
        }
        else if(msg.getMessage().equals("@@bg3")) {
            message_List.setBackgroundResource(R.drawable.background3);
        }
        else if(msg.getMessage().equals("@@bg4")) {
            message_List.setBackgroundResource(R.drawable.background4);
        }

    }

    public void saveMessage(){
        Log.d("save", "message save = ");

        String FILE_TO_SAVE;

        FILE_TO_SAVE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"P2P-Chat-And-File-Sharing-App"+File.separator+"Saved_Messages";
        Log.d("problem", "doInBackground: "+FILE_TO_SAVE);
        File directory = new File(FILE_TO_SAVE);
        if(!directory.exists()){
            directory.mkdirs();
        }
        FILE_TO_SAVE += File.separator+ipAddress+".txt";

//        File root = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
//        File dir = new File(root.getAbsolutePath() +File.separator+ "download");
//        dir.mkdirs();
//        String kk=File.separator+ipAddress+".txt";

        File file = new File(FILE_TO_SAVE);
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            for(Message message: messageArray){

                String s = "";
                if(message.isSent()){

                    s += "1:";
                }
                else {
                    s += "2:";
                }

                s += message.getMessage();
                Log.d("save", "message = " + s);
                pw.println(s);
                Log.d("save", "After PrintWriter = " + s);
            }
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("save", "File not found");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("save", "I/O exception");
        }
        String stored="Saved messages stored in "+FILE_TO_SAVE;
        Toast.makeText(this, stored, Toast.LENGTH_LONG).show();

    }

}
