package com.han.model;

/**
 * Created by Administrator on 10/18/2015.
 */
public class ProfileModel {
    String following_count, follower_count, post_count;
    double longitude, latitude;
    String status, isRegistered, photo, aboutMe, name, fb_id;
    String id, deviceType, purchased;
    String isFriend, gender, email;

    public String GetGender() {
        return gender;
    }

    public void SetGender(String itemTitle) {
        this.gender= itemTitle;
    }

    public String GetEmail() {
        return email;
    }

    public void SetEmail(String itemTitle) {
        this.email = itemTitle;
    }

    public String GetIsFriend() {
        return isFriend;
    }

    public void SetIsFriend(String itemTitle) {
        this.isFriend = itemTitle;
    }
    public String GetId() {
        return id;
    }

    public void SetId(String itemTitle) {
        this.id = itemTitle;
    }
    public String GetDeviceType() {
        return deviceType;
    }

    public void SetDeviceType(String itemTitle) {
        this.deviceType = itemTitle;
    }
    public String GetPurchased() {
        return purchased;
    }

    public void SetPurchased(String itemTitle) {
        this.purchased = itemTitle;
    }
    public String GetFollowingCount() {
        return following_count;
    }

    public void SetFollowingCount(String itemTitle) {
        this.following_count = itemTitle;
    }
    public String GetFollowerCount() {
        return follower_count;
    }

    public void SetFollowerCount(String itemTitle) {
        this.follower_count = itemTitle;
    }
    public String GetPostCount() {
        return post_count;
    }

    public void SetPostCount(String itemTitle) {
        this.post_count = itemTitle;
    }
    public double GetLogitude() {
        return longitude;
    }

    public void SetLogitude(double itemTitle) {
        this.longitude = itemTitle;
    }
    public double GetLatitude() {
        return latitude;
    }

    public void SetLatitude(double itemTitle) {

        this.latitude =itemTitle;
    }
    public String GetStatus() {
        return status;
    }

    public void SetStatus(String itemTitle) {
        this.status = itemTitle;
    }
    public String GetIsRegistered() {
        return isRegistered;
    }

    public void SetIsRegistered(String itemTitle) {
        this.isRegistered = itemTitle;
    }
    public String GetPhotoUrl() {
        return photo;
    }

    public void SetPhotoUrl(String itemTitle) {
        this.photo = itemTitle;
    }
    public String GetAboutMe() {
        return aboutMe;
    }

    public void SetAboutMe(String itemTitle) {
        this.aboutMe = itemTitle;
    }
    public String GetName() {
        return name;
    }

    public void SetName(String itemTitle) {
        this.name = itemTitle;
    }
    public String GetFbId() {
        return fb_id;
    }

    public void SetFbId(String itemTitle) {
        this.fb_id= itemTitle;
    }

}
