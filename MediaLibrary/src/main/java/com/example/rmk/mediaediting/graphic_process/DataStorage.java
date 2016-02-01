package com.example.rmk.mediaediting.graphic_process;

import android.graphics.Bitmap;

/**
 * Created by RMK on 10/19/2015.
 */
public class DataStorage {

    private static DataStorage instance = null;
    private Bitmap savedBitmap;
    private Bitmap lastResultBitmap;
    private Bitmap progressBitmap;
    private boolean isModified = false;
    private String editedImageFilePath;
    private int originalImageWidth;
    private int originalImageHeight;
    private Bitmap originalBitmap;

    private String firstVideoFilePath;
    private String lastVideoFilePath;
    private int videoWidth;
    private int videoHeight;
    private boolean downloadOrNot = false;
    private boolean videoIsModified = false;
    private Bitmap coverImageBitmap;
    private int videoEditStart = 0;
    private int videoTimeLength = 1;
    private int originalVideoWidth;
    private int originalVideoHeight;

    private String invitationId = "406";




    private String FFMpegPath;
    private int screenWidth, screenHeight;

    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }
    public void setOriginalVideoWidthAndHeight(int width, int height){
        this.originalVideoWidth = width;
        this.originalVideoHeight = height;
    }
    public int getOriginalVideoWidth(){
        return originalVideoWidth;
    }
    public int getOriginalVideoHeight(){
        return originalVideoHeight;
    }
    public void setOriginalBitmap(Bitmap bitmap){
        this.originalBitmap = bitmap;
    }
    public Bitmap getOriginalBitmap(){
        return this.originalBitmap;
    }
    public void setOriginalImageWidthAndHeight(int width, int height){
        this.originalImageHeight = height;
        this.originalImageWidth = width;
    }
    public int getOriginalImageWidth(){
        return originalImageWidth;
    }
    public int getOriginalImageHeight(){
        return originalImageHeight;
    }
    public String getEditedImageFilePath(){ return  editedImageFilePath;}
    public void setEditedImageFilePath(String imagePath){
        this.editedImageFilePath = imagePath;
    }
    public void setInvitationId(String string){
        this.invitationId = string;
    }
    public String getInvitationId(){ return invitationId; }
    public void setVideoTimeLength(int timeLength){
        this.videoTimeLength = timeLength;
    }
    public int getVideoTimeLength(){
        return videoTimeLength;
    }
    public void setVideoEditStart(int i){
        this.videoEditStart = i;
    }
    public int getVideoEditStart(){
        return videoEditStart;
    }
    public void setCoverImageBitmap(Bitmap bitmap){ this.coverImageBitmap = bitmap;}
    public Bitmap getCoverImageBitmap(){
        return coverImageBitmap;
    }
    public void setVideoIsModified(Boolean flag){
        this.videoIsModified = true;
    }
    public Boolean getVideoIsModified(){
        return videoIsModified;
    }
    public void setDownloadOrNot(Boolean flag){
        this.downloadOrNot = flag;
    }
    public boolean getDownloadOrNot(){
        return this.downloadOrNot;
    }
    public void setFFMpegPath(String string){
        this.FFMpegPath = string;
    }
    public String getFFMpegPath(){
        return this.FFMpegPath;
    }

    public void setVideoWidthAndHeight(int w, int h){
        this.videoHeight = h; this.videoWidth = w;
    }
    public int getVideoWidth(){ return videoWidth;}
    public int getVideoHeight(){ return videoHeight;}

    public void setFirstVideoFilePath(String string){
        this.firstVideoFilePath = string;
    }
    public void setLastVideoFilePath(String string){
        this.lastVideoFilePath = string;
    }
    public String getFirstVideoFilePath(){
        return firstVideoFilePath;
    }
    public String getLastVideoFilePath(){
        return lastVideoFilePath;
    }
    public void setProgressBitmap(Bitmap bitmap){
        this.progressBitmap = bitmap;
    }
    public Bitmap getProgressBitmap(){
        return progressBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.savedBitmap = bitmap;
    }
    public Bitmap getBitmap() {
        return savedBitmap;
    }

    public void setisModified(int i){
        if (i == 1)
            this.isModified = true;
        else
            this.isModified = false;
    }
    public boolean getisModified(){
        return isModified;
    }

    public Bitmap getLastResultBitmap() {
        return lastResultBitmap;
    }
    public void setLastResultBitmap(Bitmap lastResultBitmap) {
        this.lastResultBitmap = lastResultBitmap;
    }
    public void save() {
        if (lastResultBitmap != null) {
            if (savedBitmap != lastResultBitmap && savedBitmap != null) {
                savedBitmap.recycle();
            }
            savedBitmap = lastResultBitmap;
            lastResultBitmap = null;
        }
    }

    public void setScreenWidthAndHeight(int w, int h){
        this.screenWidth = w; this.screenHeight = h;
    }
    public int getScreenWidth(){ return screenWidth;}
    public int getScreenHeight() { return  screenHeight; }


}
