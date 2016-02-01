package com.han.model;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 9/29/2015.
 */
public class UserModel {
    String strUsername;
    String strPhoto;
    String strMessage;

    String strSender;
    String strIsFollowing;

    String strEmail, strUserId;
    int time;
    String article_id;
    String articleUrl;
    String content;
    Bitmap bitmap;

    public Bitmap GetBitmap() {
        return bitmap;
    }
    public void SetBitmap(Bitmap itemTitle) {
        this.bitmap = itemTitle;
    }


    public int GetTime() {
        return time;
    }

    public void SetTime(int itemTitle) {
        this.time = itemTitle;
    }
    public String GetArticleId() {
        return article_id;
    }

    public void SetArticleId(String itemTitle) {
        this.article_id = itemTitle;
    }
    public String GetArticleUrl() {
        return articleUrl;
    }

    public void SetArticleUrl(String itemTitle) {
        this.articleUrl = itemTitle;
    }
    public String GetContent() {
        return content;
    }

    public void SetContent(String itemTitle) {
        this.content = itemTitle;
    }


     public String GetEmail() {
        return strEmail;
    }

    public void SetEmail(String itemTitle) {
        this.strEmail = itemTitle;
    }

    public String GetId() {
        return strUserId;
    }

    public void SetId(String itemTitle) {
        this.strUserId = itemTitle;
    }


    public String GetName() {
        return strUsername;
    }

    public void SetName(String itemTitle) {
        this.strUsername = itemTitle;
    }
    public String GetPhoto() {
        return strPhoto;
    }

    public void SetPhoto(String itemTitle) {
        this.strPhoto = itemTitle;
    }
    public String GetMessage() {
        return strMessage;
    }

    public void SetMessage(String itemTitle) {
        this.strMessage = itemTitle;
    }

    public String GetSender() {
        return strSender;
    }

    public void SetSender(String itemTitle) {
        this.strSender = itemTitle;
    }
    public String GetIsFollowing() {
        return strIsFollowing;
    }

    public void SetIsFollowing(String itemTitle) {
        this.strIsFollowing = itemTitle;
    }

}
