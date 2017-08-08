package com.e3lue.us.model;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Leo on 2017/4/20.
 */

public class Diary implements Serializable {
    private int ID;
    private int DiaryDayID;
    private int ContactPersonID;
    private String ContactPersonName;
    private String CreateDate;
    private int CompanyID;
    private float Spend;
    private String CompanyName;
    private String Address;
    private String DateStr;
    private String Picture;
    private String DiaryContent;
    private int UserID;
    private String UserName;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }




    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getDiaryDayID() {
        return DiaryDayID;
    }

    public void setDiaryDayID(int diaryDayID) {
        DiaryDayID = diaryDayID;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public float getSpend() {
        return Spend;
    }

    public void setSpend(float spend) {
        Spend = spend;
    }
    
    public int getContactPersonID() {
        return ContactPersonID;
    }

    public void setContactPersonID(int contactPersonID) {
        ContactPersonID = contactPersonID;
    }

    public String getContactPersonName() {
        return ContactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        ContactPersonName = contactPersonName;
    }

    public int getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(int companyID) {
        CompanyID = companyID;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDateStr() {
        return DateStr;
    }

    public void setDateStr(String dateStr) {
        DateStr = dateStr;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getDiaryContent() {
        return DiaryContent;
    }

    public void setDiaryContent(String diaryContent) {
        DiaryContent = diaryContent;
    }


}
