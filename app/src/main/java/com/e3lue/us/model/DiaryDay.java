package com.e3lue.us.model;

/**
 * Created by Leo on 2017/5/12.
 */

public class DiaryDay {
    private String CreateDate;
    private int State;
    private String StateName;
    private String LastReceiveId;
    private int UserID;
    private String UserName;

    private float Spend;
    private int CompanyCount;

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public String getLastReceiveId() {
        return LastReceiveId;
    }

    public void setLastReceiveId(String lastReceiveId) {
        LastReceiveId = lastReceiveId;
    }

    public float getSpend() {
        return Spend;
    }

    public void setSpend(float spend) {
        Spend = spend;
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

    public int getCompanyCount() {
        return CompanyCount;
    }

    public void setCompanyCount(int companyCount) {
        CompanyCount = companyCount;
    }

}
