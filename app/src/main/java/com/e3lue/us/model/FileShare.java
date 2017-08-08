package com.e3lue.us.model;

import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Leo on 2017/6/23.
 */
public class FileShare implements Serializable {

    private static final long serialVersionUID = 2072893447591548402L;

    public String FileTitle;
    public String DocumentMaker;
    public String FileName;
    public int priority;
    public String Limits;
    public String DateStr;
    public String CreateDate;

    public String getFileTitle() {
        return FileTitle;
    }

    public void setFileTitle(String fileTitle) {
        FileTitle = fileTitle;
    }

    public String getDocumentMaker() {
        return DocumentMaker;
    }

    public void setDocumentMaker(String documentMaker) {
        DocumentMaker = documentMaker;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getLimits() {
        return Limits;
    }

    public void setLimits(String limits) {
        Limits = limits;
    }

    public String getDateStr() {
        return DateStr;
    }

    public void setDateStr(String dateStr) {
        DateStr = dateStr;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }



    public FileShare() {
        Random random = new Random();
        priority = random.nextInt(100);
    }
}
