package com.e3lue.us.model;

/**
 * Created by Leo on 2016/10/9.
 */

public class WorkFlowTask {
    private int ID;
    private String StepName;
    private int Type;
    private String SenderName;
    private String ReceiveTime;
    private String ReceiveName;
    private String CompletedTime1;
    private String Comment;
    private int Status;
    private String Note;

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

    public String getStepName() {
        return StepName;
    }

    public void setStepName(String stepName) {
        StepName = stepName;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getReceiveTime() {
        return ReceiveTime;
    }

    public void setReceiveTime(String senderTime) {
        ReceiveTime = senderTime;
    }

    public String getReceiveName() {
        return ReceiveName;
    }

    public void setReceiveName(String receiveName) {
        ReceiveName = receiveName;
    }

    public String getCompletedTime1() {
        return CompletedTime1;
    }

    public void setCompletedTime1(String completedTime1) {
        CompletedTime1 = completedTime1;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }


}
