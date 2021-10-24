package com.savio.gotanyidea;

public class Coment {

    private String comentID;
    private String userComentID;
    private String comentText;
    private long timestamp;
    private String userNameComent;
    private String urlProfilePhotoComent;


    public Coment() {
    }

    public String getComentID() {
        return comentID;
    }

    public void setComentID(String comentID) {
        this.comentID = comentID;
    }

    public String getUserComentID() {
        return userComentID;
    }

    public void setUserComentID(String userComentID) {
        this.userComentID = userComentID;
    }

    public String getComentText() {
        return comentText;
    }

    public void setComentText(String comentText) {
        this.comentText = comentText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserNameComent() {
        return userNameComent;
    }

    public void setUserNameComent(String userNameComent) {
        this.userNameComent = userNameComent;
    }

    public String getUrlProfilePhotoComent() {
        return urlProfilePhotoComent;
    }

    public void setUrlProfilePhotoComent(String urlProfilePhotoComent) {
        this.urlProfilePhotoComent = urlProfilePhotoComent;
    }
}
