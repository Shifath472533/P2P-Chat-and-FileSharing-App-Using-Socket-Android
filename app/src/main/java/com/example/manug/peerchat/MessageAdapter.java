package com.example.manug.peerchat;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
public class MessageAdapter extends BaseAdapter{

    Context context;
    ArrayList<Message> arr = new ArrayList<>();

    public MessageAdapter(Context context,ArrayList<Message> arr) {

        this.context = context;
        this.arr = arr;

        for(Message mssg: arr){
            String sst = mssg.getMessage();
            Log.d("problem","              "+sst);
        }
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int i) {
        return arr.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //View listItemView=convertView;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.message_list,parent,false);
        }
        Message currentMessage= (Message) getItem(position);
        String message=currentMessage.getMessage();

        //Log.d("problem","    Current Message:    " + message);
        TextView sent=(TextView) convertView.findViewById(R.id.list_sent);
        TextView received= (TextView) convertView.findViewById(R.id.list_received);

        sent.setText("");
        sent.setVisibility(View.GONE);
        received.setText("");
        received.setVisibility(View.GONE);

        if(currentMessage.isSent()){
            sent.setText(message);
            sent.setVisibility(View.VISIBLE);
            //sent.setVisibility(View.VISIBLE);
        }
        else{
            received.setText(message);
            received.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
