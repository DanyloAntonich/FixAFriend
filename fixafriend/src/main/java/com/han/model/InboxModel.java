package com.han.model;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 10/20/2015.
 */
public class InboxModel {
    String id;
    String photoURL;
    String articlAttach;
    int limite;
    int sendDate;
    String sender_id;
    String invitationId;
    String name;
    String senderEmail;
    String articleType;
    Bitmap bitmap;

    public Bitmap GetBitmap() {
        return bitmap;
    }
    public void SetBitmap(Bitmap itemTitle) {
        this.bitmap = itemTitle;
    }

    public String GetArticleAttach() {
        return articlAttach;
    }
    public void SetArticleAttach(String itemTitle) {
        this.articlAttach = itemTitle;
    }
    public String GetSenderId() {
        return sender_id;
    }
    public void SetSenderId(String itemTitle) {
        this.sender_id = itemTitle;
    }
    public String GetInvitationId() {
        return invitationId;
    }
    public void SetInvitationId(String itemTitle) {
        this.invitationId = itemTitle;
    }
    public String GetPhotoURL() {
        return photoURL;
    }
    public void SetPhotoURL(String itemTitle) {
        this.photoURL = itemTitle;
    }

    public int GetLimite() {
        return limite;
    }
    public void SetLimite(int itemTitle) {
        this.limite = itemTitle;
    }
    public int GetSendDate() {
        return sendDate;
    }
    public void SetSendDate(int itemTitle) {
        this.sendDate = itemTitle;
    }
    public String GetName() {
        return name;
    }
    public void SetName(String itemTitle) {
        this.name = itemTitle;
    }

    public String GetSenderEmail() {
        return senderEmail;
    }
    public void SetSenderEmail(String itemTitle) {
        this.senderEmail = itemTitle;
    }

    public String GetArticleType() {
        return articleType;
    }
    public void SetArticleType(String itemTitle) {
        this.articleType = itemTitle;
    }
}
