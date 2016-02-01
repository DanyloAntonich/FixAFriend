package com.han.model;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 10/17/2015.
 */
public class MessageModel {
    String strUserId;
    String strPhotoURL;
    String strMessage;
    String strUnreadCount;
    String strEmail;
    String strName;
    Bitmap bitmap;

    public Bitmap GetBitmap() {
        return bitmap;
    }
    public void SetBitmap(Bitmap itemTitle) {
        this.bitmap = itemTitle;
    }



    public String GetMessage() {
        return strMessage;
    }

    public void SetMessage(String itemTitle) {
        this.strMessage = itemTitle;
    }
    public String GetName() {
        return strName;
    }

    public void SetName(String itemTitle) {
        this.strName = itemTitle;
    }
    public String GetUserId() {
        return strUserId;
    }

    public void SetUserId(String itemTitle) {
        this.strUserId = itemTitle;
    }
    public String GetPhotoURL() {
        return strPhotoURL;
    }

    public void SetPhotoURL(String itemTitle) {
        this.strPhotoURL = itemTitle;
    }
    public String GetUnreadCount() {
        return strUnreadCount;
    }

    public void SetUnreadCount(String itemTitle) {
        this.strUnreadCount = itemTitle;
    }
    public String GetEmail() {
        return strEmail;
    }

    public void SetEmail(String itemTitle) {
        this.strEmail = itemTitle;
    }
}
