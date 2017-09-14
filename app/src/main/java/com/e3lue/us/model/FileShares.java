package com.e3lue.us.model;

/**
 * Created by Enzate on 2017/8/25.
 */

public class FileShares implements Comparable<FileShares> {

    /**
     * id : 1
     * pId : 0
     * path : 1PPT
     * type : fold
     */

    private int id;
    private int pId;
    private String path;
    private String type;
    private Integer MobileSortId;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPId() {
        return pId;
    }

    public void setPId(int pId) {
        this.pId = pId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setMobileSortId(Integer mobileSortId) {
        MobileSortId = mobileSortId;
    }

    public void setType(String type) {
        this.type = type;
    }
    public int compareTo(FileShares arg0) {
        return this.getMobileSortId().compareTo(arg0.getMobileSortId());
    }
    public Integer getMobileSortId() {
        return MobileSortId;
    }
}
