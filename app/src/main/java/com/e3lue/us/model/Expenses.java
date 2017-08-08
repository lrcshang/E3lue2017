package com.e3lue.us.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Leo on 2016/8/12.
 */
public class Expenses implements Serializable, Parcelable {
    private int ID;
    private int UserID;
    private String UserName;
    private String CreateTime;
    private double Amount;
    private String Address;
    private String Type1;
    private String Picture;
    private int State;
    private String StateName;
    private String DateStr;
    private String Note;
    private int DiaryID;
    private String Type2;
    private String DeptName;
    private String LastReceiveId;

    public String getType1() {
        return Type1;
    }

    public void setType1(String type1) {
        Type1 = type1;
    }

    public String getType2() {
        return Type2;
    }

    public void setType2(String type2) {
        Type2 = type2;
    }
    public String getLastReceiveId() {
        return LastReceiveId;
    }

    public void setLastReceiveId(String lastReceiveId) {
        LastReceiveId = lastReceiveId;
    }

    public String getDeptName() {
        return DeptName;
    }

    public void setDeptName(String deptName) {
        DeptName = deptName;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public String getDateStr() {
        return DateStr;
    }

    public void setDateStr(String dateStr) {
        DateStr = dateStr;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

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

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public int getDiaryID() {
        return DiaryID;
    }

    public void setDiaryID(int diaryID) {
        DiaryID = diaryID;
    }

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeDouble(Amount);
        dest.writeString(CreateTime);
        dest.writeString(Note);
        dest.writeString(LastReceiveId);
    }

    public static final Parcelable.Creator<Expenses> CREATOR = new Creator<Expenses>() {
        public Expenses createFromParcel(Parcel source) {
            Expenses expenses = new Expenses();
            expenses.ID = source.readInt();
            expenses.Amount = source.readDouble();
            expenses.CreateTime = source.readString();
            expenses.Note = source.readString();
            expenses.LastReceiveId = source.readString();
            return expenses;
        }

        public Expenses[] newArray(int size) {
            return new Expenses[size];
        }
    };
}
