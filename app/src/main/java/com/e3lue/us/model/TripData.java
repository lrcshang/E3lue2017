package com.e3lue.us.model;

/**
 * Created by Enzate on 2017/11/3.
 */

public class TripData {
    /* UserID 人员ID int
     UserName 人员名称 String
     PlanDate 日期 Date
     Locale  地点 String
     CompanyName 公司 String
     ContactPerson 接洽对象 String
     Departs 出发地点 String
     Destination 到达地点 String
     Transport 交通方式 String
     Content 工作内容 String
     Fee1 交通 int
     Fee2 差补 int
     Fee3 招待 int
     Note 招待说明 String*/
    public TripData() {
    }

    public int getUserID() {
        return UserID;
    }

    public String getUserName() {
        return UserName;
    }

    public String getPlanDate() {
        return PlanDate;
    }

    public String getLocale() {
        return Locale;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public String getContactPerson() {
        return ContactPerson;
    }

    public String getDeparts() {
        return Departs;
    }

    public String getDestination() {
        return Destination;
    }

    public String getTransport() {
        return Transport;
    }

    public String getContent() {
        return Content;
    }

    public int getFee1() {
        return Fee1;
    }

    public int getFee2() {
        return Fee2;
    }

    public int getFee3() {
        return Fee3;
    }

    public String getNote() {
        return Note;
    }

    int UserID;
    String UserName;

    public void setUserID(int userID) {
        UserID = userID;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setPlanDate(String planDate) {
        PlanDate = planDate;
    }

    public void setLocale(String locale) {
        Locale = locale;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public void setContactPerson(String contactPerson) {
        ContactPerson = contactPerson;
    }

    public void setDeparts(String departs) {
        Departs = departs;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public void setTransport(String transport) {
        Transport = transport;
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setFee1(int fee1) {
        Fee1 = fee1;
    }

    public void setFee2(int fee2) {
        Fee2 = fee2;
    }

    public void setFee3(int fee3) {
        Fee3 = fee3;
    }

    public void setNote(String note) {
        Note = note;
    }

    String PlanDate; // 日期 Date
    String Locale;
    String CompanyName;
    String ContactPerson;
    String Departs;
    String Destination;
    String Transport;
    String Content;
    int Fee1;
    int Fee2;
    int Fee3;
    String Note;
}
