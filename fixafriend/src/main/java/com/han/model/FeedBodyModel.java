package com.han.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Administrator on 9/29/2015.
 */
public class FeedBodyModel {
    String strFeedId;
    String strArticleId;
    String strPosterName;
    String strOwnerEmail;
    String strDescription;
    int time;
    String strLikeCount;
    String strCommentCount;
    String strIslike;
//    String strCommentBody;
    String strAttatchURL;
    String strAttatchType;
    String strPosterId;
    String strOwnerId;
    String strTag;
    String strOwnerPhoto;

    Bitmap bitmap;
    boolean mediaLoaded;
    ArrayList<CommentModel> arrCommentModel= new ArrayList<CommentModel>();
    public ArrayList<CommentModel> GetArrCommentModel() {
        return arrCommentModel;
    }

    public void SetArrCommentModel(ArrayList<CommentModel> itemTitle) {
        this.arrCommentModel = itemTitle;
    }
    public void SetBitmap(Bitmap itemTitle) {
        this.bitmap = itemTitle;
    }

    public Bitmap GetBitmap() {
        return bitmap;
    }
    public void SetFlag(Boolean itemTitle) {
        this.mediaLoaded = itemTitle;
    }

    public boolean GetFlag() {
        return mediaLoaded;
    }
    public String GetTag() {
        return strTag;
    }

    public void SetTag(String itemTitle) {
        this.strTag = itemTitle;
    }

    public String GetOwnerPhoto() {
        return strOwnerPhoto;
    }

    public void SetOwnerPhoto(String itemTitle) {
        this.strOwnerPhoto = itemTitle;
    }
    public String GetFeedId() {
        return strFeedId;
    }

    public void SetFeedId(String itemTitle) {
        this.strFeedId = itemTitle;
    }

    public String GetAttatchType() {
        return strAttatchType;
    }

    public void SetAttatchType(String itemTitle) {
        this.strAttatchType = itemTitle;
    }

    public String GetPosterId() {
        return strPosterId;
    }

    public void SetPosterId(String itemTitle) {
        this.strPosterId = itemTitle;
    }

    public String GetOwnerId() {
        return strOwnerId;
    }

    public void SetOwnerId(String itemTitle) {
        this.strOwnerId = itemTitle;
    }

    public String GetArticleId() {
        return strArticleId;
    }

    public void SetArticleId(String itemTitle) {
        this.strArticleId = itemTitle;
    }

    public String GetPosterName() {
        return strPosterName;
    }

    public void SetPosterName(String itemTitle) {
        this.strPosterName = itemTitle;
    }
    public String GetOwnerEmail() {
        return strOwnerEmail;
    }

    public void SetOwnerEmail(String itemTitle) {
        this.strOwnerEmail = itemTitle;
    }
    public String GetDescription() {
        return strDescription;
    }

    public void SetDescriptioin(String itemTitle) {
        this.strDescription = itemTitle;
    }
    public int GetTime() {
        return time;
    }

    public void SetTime(int itemTitle) {
        this.time = itemTitle;
    }
    public String GetLikeCount() {
        return strLikeCount;
    }

    public void SetLikeCount(String itemTitle) {
        this.strLikeCount = itemTitle;
    }

    public String GetCommentCount() {
        return strCommentCount;
    }

    public void SetCommentCount(String itemTitle) {
        this.strCommentCount = itemTitle;
    }
    public String GetIslike() {
        return strIslike;
    }

    public void SetIslike(String itemTitle) {
        this.strIslike = itemTitle;
    }
//    public String GetCommentBody() {
//        return strCommentBody;
//    }
//
//    public void SetCommentBody(String itemTitle) {
//        this.strCommentBody = itemTitle;
//    }
    public String GetAttachURL() {
        return strAttatchURL;
    }

    public void SetAttachURL(String itemTitle) {
        this.strAttatchURL = itemTitle;
    }


}
