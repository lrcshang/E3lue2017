package com.e3lue.us.model;

/**
 * Created by Enzate on 2017/7/19.
 */

public class UserCheckInfo {

    /**
     * ID : 12149
     * GpsID : 14739
     * SaleChannel : 1
     * UserID : 1174
     * UserName : 吕从瑞
     * CreateTime : 2017/7/18 17:51:00
     * Address : 中国广东省深圳市宝安区航空路7
     * DateStr : 20170718
     * Picture : l_1707181757412364595.jpeg
     * Note :
     * DelFlag : 0
     */

    private int ID;
    private String UserName;
    private String Address;
    private String DateStr;
    private String Picture;
    private String Note;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getDateStr() {
        return DateStr;
    }

    public void setDateStr(String DateStr) {
        this.DateStr = DateStr;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String Picture) {
        this.Picture = Picture;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String Note) {
        this.Note = Note;
    }
}
