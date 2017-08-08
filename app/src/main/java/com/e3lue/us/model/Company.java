package com.e3lue.us.model;

/**
 * Created by Leo on 2016/8/14.
 */
public class Company {
    private int ID;
    private String Name;
    private String ShortName;
    private String Pym;
    private String Address;
    private String ResponPerson;

    public String getResponPerson() {
        return ResponPerson;
    }

    public void setResponPerson(String responPerson) {
        ResponPerson = responPerson;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String shortName) {
        ShortName = shortName;
    }

    public String getPym() {
        return Pym;
    }

    public void setPym(String pym) {
        Pym = pym;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }



}
