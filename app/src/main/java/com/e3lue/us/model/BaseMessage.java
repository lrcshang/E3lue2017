package com.e3lue.us.model;

/**
 * Created by Leo on 2017/4/28.
 */

public class BaseMessage {
    private String MessageTitle;
    private String MessageContent;
    private String RelationURL;
    private String MessagePicture;

    public String getMessageTitle() {
        return MessageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        MessageTitle = messageTitle;
    }

    public String getMessageContent() {
        return MessageContent;
    }

    public void setMessageContent(String messageContent) {
        MessageContent = messageContent;
    }

    public String getRelationURL() {
        return RelationURL;
    }

    public void setRelationURL(String relationURL) {
        RelationURL = relationURL;
    }

    public String getMessagePicture() {
        return MessagePicture;
    }

    public void setMessagePicture(String messagePicture) {
        MessagePicture = messagePicture;
    }



}
