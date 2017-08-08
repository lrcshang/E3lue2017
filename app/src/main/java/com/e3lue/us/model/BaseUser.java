package com.e3lue.us.model;

/**
 * Created by Leo on 2016/10/8.
 */

public class BaseUser {
    private int UserId;
    private int EmpId;
    private String Code;
    private String Name;
    private int IsAdmin;
    private int IsLeader;
    private int SaleChannel;
    private String Memo;

    public int getSaleChannel() {
        return SaleChannel;
    }

    public void setSaleChannel(int saleChannel) {
        SaleChannel = saleChannel;
    }

    public int getIsLeader() {
        return IsLeader;
    }

    public void setIsLeader(int isLeader) {
        IsLeader = isLeader;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public int getIsAdmin() {
        return IsAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        IsAdmin = isAdmin;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getEmpId() {
        return EmpId;
    }

    public void setEmpId(int empId) {
        EmpId = empId;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

}
