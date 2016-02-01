package com.han.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 9/30/2015.
 */
public class FeedModel implements Parcelable {
    FeedBodyModel fm;
    FeedBannerModel fbm;
//    ArrayList<CommentModel> arrCM;
//
//    public ArrayList<CommentModel> GetCommentModel() {
//        return arrCM;
//    }
//
//    public void SetCommentModel(ArrayList<CommentModel> itemTitle) {
//        this.arrCM = itemTitle;
//    }

    public FeedModel(Parcel in) {
    }

    public static final Creator<FeedModel> CREATOR = new Creator<FeedModel>() {
        @Override
        public FeedModel createFromParcel(Parcel in) {
            return new FeedModel(in);
        }

        @Override
        public FeedModel[] newArray(int size) {
            return new FeedModel[size];
        }
    };

    public FeedModel() {

    }

    public FeedBodyModel GetFeedBodyModel() {
        return fm;
    }

    public void SetFeedBodyModel(FeedBodyModel itemTitle) {
        this.fm = itemTitle;
    }

    public FeedBannerModel GetFeedBannerModel() {
        return fbm;
    }

    public void SetFeedBannerModel(FeedBannerModel itemTitle) {
        this.fbm = itemTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
