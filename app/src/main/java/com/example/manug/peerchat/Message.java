package com.example.manug.peerchat;

import java.io.File;
import java.io.Serializable;

public class Message implements Serializable{
    String message = null;
    int type;
    int fileOrNot = 0;

    int bg = 0;
    byte [] mybytearray;

    public void setBG(){
        bg = 1;
    }

    public boolean isBackground(){
        if(bg == 1)
            return true;
        else
            return false;
    }

    public boolean isFile(){
        if(fileOrNot == 1)
            return true;
        else
            return false;
    }

    public Message(String message, byte [] mybytearray, int type){
        this.message=message;
        this.mybytearray = mybytearray;
        this.type = type;
        fileOrNot = 1;
    }

    public Message(String message,int type){
        this.message=message;
        this.type=type;
    }
    public boolean isSent(){
        if(type==0){
            return true;
        }
        return false;
    }
    public String getMessage(){
        return message;
    }

    public byte[] getMybytearray() {
        return mybytearray;
    }
}
