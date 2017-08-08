package com.e3lue.us.model;

/**
 * Created by Leo on 2017/5/25.
 */

public class DiaryMessage {
    private int ID;
    private int DiaryID;
    private int UserID;
    private String UserName;
    private String CreateTime;
    private String Message;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getDiaryID() {
        return DiaryID;
    }

    public void setDiaryID(int diaryID) {
        DiaryID = diaryID;
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

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }


}
