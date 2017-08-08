package com.e3lue.us.model;

/**
 * Created by Leo on 2017/4/13.
 */

public class NameCardFile {
    private String CreateTime;
    private String DateStr;
    private String ImgUrl;
    private int Side;

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getDateStr() {
        return DateStr;
    }

    public void setDateStr(String dateStr) {
        DateStr = dateStr;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public int getSide() {
        return Side;
    }

    public void setSide(int side) {
        Side = side;
    }

}
