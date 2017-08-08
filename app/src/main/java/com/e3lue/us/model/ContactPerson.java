package com.e3lue.us.model;


import java.io.Serializable;

/**
 * Created by Leo on 2017/4/12.
 */

public class ContactPerson implements Serializable {


    private String Index;
    private String Name;
    private String Mobile;
    private int CompanyID;
    private int Sex;
    private String CompanyName;
    private String Note;
    private int ID;
    private int State;
    private String StateName;

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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ContactPerson(String name) {
        this.Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public int getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(int companyID) {
        CompanyID = companyID;
    }

    public int getSex() {
        return Sex;
    }

    public void setSex(int sex) {
        Sex = sex;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public void setIndex(String index) {
        Index = index;
    }

    public String getIndex() {
        return Index;
    }

}
