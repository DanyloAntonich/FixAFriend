package com.han.model;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 10/19/2015.
 */
public class ChatModel {
    String message;
    String photoURL;
    int time;
    String sender;
    String receiver;
    Bitmap bitmap;

    public Bitmap GetBitmap() {
        return bitmap;
    }
    public void SetBitmap(Bitmap itemTitle) {
        this.bitmap = itemTitle;
    }


    public String GetSenderId() {
        return sender;
    }
    public void SetSenderId(String itemTitle) {
        this.sender = itemTitle;
    }
    public String GetReceiverId() {
        return receiver;
    }
    public void SetReceiverId(String itemTitle) {
        this.receiver = itemTitle;
    }
    public String GetPhotoURL() {
        return photoURL;
    }
    public void SetPhotoURL(String itemTitle) {
        this.photoURL = itemTitle;
    }

    public int GetTime() {
        return time;
    }
    public void SetTime(int itemTitle) {
        this.time = itemTitle;
    }

    public String GetMessage() {
        return message;
    }
    public void SetMessage(String itemTitle) {
        this.message = itemTitle;
    }
}
