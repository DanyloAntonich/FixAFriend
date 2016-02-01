package com.han.model;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 9/30/2015.
 */
public class FeedBannerModel {
    String strOwnername;
    String strOwnerPhoto;
    String strEditNumber;
    String strTime;
    int posted_date;
    Bitmap bitmap;

    public Bitmap GetBitmap() {
        return bitmap;
    }
    public void SetBitmap(Bitmap itemTitle) {
        this.bitmap = itemTitle;
    }

    public String GetUsername() {
        return strOwnername;
    }

    public void SetUsername(String itemTitle) {
        this.strOwnername = itemTitle;
    }
    public String GetPhoto() {
        return strOwnerPhoto;
    }

    public void SetPhoto(String itemTitle) {
        this.strOwnerPhoto = itemTitle;
    }
    public String GetTime() {
        return strTime;
    }

    public void SetTime(String itemTitle) {
        this.strTime = itemTitle;
    }
    public String GetEditNumber() {
        return strEditNumber;
    }

    public void SetEditNumber(String itemTitle) {
        this.strEditNumber = itemTitle;
    }
    public int GetPostedDate(){
        return posted_date;
    }
    public void SetPostedDate(int item){
        this.posted_date = item;
    }
}
