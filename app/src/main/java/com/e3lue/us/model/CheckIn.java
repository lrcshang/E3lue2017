package com.e3lue.us.model;

import java.io.Serializable;

/**
 * Created by Leo on 2016/8/11.
 */
public class CheckIn implements Serializable {
    private int ID;
    private int GpsID;
    private String UserID;
    private String UserName;
    private String DateStr;
    private String Picture;
    private String CreateTime;
    private String Address;
    private String Note;
    private Boolean isshow=false;
    public Boolean getIsshow() {
        return isshow;
    }

    public void setIsshow(Boolean isshow) {
        this.isshow = isshow;
    }
    public int getGpsID() {
        return GpsID;
    }

    public void setGpsID(int gpsID) {
        GpsID = gpsID;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getDateStr() {
        return DateStr;
    }

    public void setDateStr(String dateStr) {
        DateStr = dateStr;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getUserId() {
        return UserID;
    }

    public void setUserId(String userId) {
        UserID = userId;
    }

}
