package com.han.model;

/**
 * Created by Administrator on 10/14/2015.
 */
public class CommentModel {
    String comment;
    String photoURL;
    String userId;
    String name;
    int time;
    public int GetTime() {
        return time;
    }
    public void SetTime(int itemTitle) {
        this.time = itemTitle;
    }
    public String GetName() {
        return name;
    }
    public void SetName(String itemTitle) {
        this.name = itemTitle;
    }
    public String GetPhotoURL() {
        return photoURL;
    }
    public void SetPhotoURL(String itemTitle) {
        this.photoURL = itemTitle;
    }

    public String GetUserId() {
        return userId;
    }
    public void SetUserId(String itemTitle) {
        this.userId = itemTitle;
    }

    public String GetComment() {
        return comment;
    }
    public void SetComment(String itemTitle) {
        this.comment = itemTitle;
    }

}
