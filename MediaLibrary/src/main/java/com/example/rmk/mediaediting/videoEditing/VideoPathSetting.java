package com.example.rmk.mediaediting.videoEditing;

import android.os.Environment;

/**
 * Created by RMK on 11/5/2015.
 */
public class VideoPathSetting {

    private static VideoPathSetting instance = null;

    String originalVideoPath  = Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/" + "firstVideoFile.mp4";
    String newVideoPath = Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/" + "lastVideoFile.mp4";
    String audioPath = Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/" + "recordedAudioFile.3gp";
    String processImage = Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/" + "maskImage.png";
    String imageAudioPlusVideoPath = Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/" + "article.mp4";

    int audioMixOrNot = 0;


    public static VideoPathSetting getInstance() {
        if (instance == null) {
            instance = new VideoPathSetting();
        }
        return instance;
    }
    public void setAudioMixOrNot(int flag){
        this.audioMixOrNot = flag;
    }

    public int getAudioMixOrNot(){
        return this.audioMixOrNot;
    }

    public String getImageAudioPlusVideoPath(){return imageAudioPlusVideoPath;}
    public void setImageAudioPlusVideoPath(String path){
        this.imageAudioPlusVideoPath = path;
    }
    public String getAudioPath(){
        return audioPath;
    }

    public String getProcessImage(){
        return this.processImage;
    }
    public void setOriginalVideoPath(String s){
        this.originalVideoPath = s;
    }
    public void setNewVideoPath(String s){
        this.newVideoPath = s;
    }

    public String getOriginalVideoPath(){
        return originalVideoPath;
    }
    public String getNewVideoPath(){
        return newVideoPath;
    }

    public void changePath(){
        String tempPath = originalVideoPath;
        originalVideoPath = newVideoPath;
        newVideoPath = tempPath;
    }
}
