package com.han.model;

/**
 * Created by Administrator on 10/18/2015.
 */
public class LoginModel {
    String id, fb_id, about_me, status, email, deviceType, name, gender, isRegistered, photoUrl, password, accessToken, strLoginMode, purchased;

    public String GetPurchased() {
        return purchased;
    }

    public void SetPurchased(String itemTitle) {
        this.purchased = itemTitle;
    }
    public String GetLoginMode() {
        return strLoginMode;
    }

    public void SetLoginMode(String itemTitle) {
        this.strLoginMode = itemTitle;
    }
    public String GetAccessToken() {
        return accessToken;
    }

    public void SetAccessToken(String itemTitle) {
        this.accessToken = itemTitle;
    }
    public String GetId() {
        return id;
    }

    public void SetId(String itemTitle) {
        this.id = itemTitle;
    }
    public String GetFbId() {
        return fb_id;
    }

    public void SetFbId(String itemTitle) {
        this.fb_id = itemTitle;
    }
    public String GetAboutMe() {
        return about_me;
    }

    public void SetAboutMe(String itemTitle) {
        this.about_me = itemTitle;
    }
    public String GetStatus() {
        return status;
    }

    public void SetStatus(String itemTitle) {
        this.status = itemTitle;
    }
    public String GetEmail() {
        return email;
    }

    public void SetEmail(String itemTitle) {
        this.email = itemTitle;
    }
    public String GetDeviceType() {
        return deviceType;
    }

    public void SetDeviceType(String itemTitle) {
        this.deviceType = itemTitle;
    }
    public String GetName() {
        return name;
    }

    public void SetName(String itemTitle) {
        this.name = itemTitle;
    }
    public String GetGender() {
        return gender;
    }

    public void SetGender(String itemTitle) {
        this.gender = itemTitle;
    }
    public String GetIsRegistered() {
        return isRegistered;
    }

    public void SetIsRegistered(String itemTitle) {
        this.isRegistered = itemTitle;
    }
    public String GetPhotoUrl() {
        return photoUrl;
    }

    public void SetPhotoUrl(String itemTitle) {
        this.photoUrl = itemTitle;
    }
    public String GetPassword() {
        return password;
    }

    public void SetPassword(String itemTitle) {
        this.password = itemTitle;
    }
}
