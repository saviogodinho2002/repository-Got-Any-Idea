package com.savio.gotanyidea;

public class Coment {

    private String comentID;
    private String fromtID;
    private String comentText;
    private long timestamp;


    public Coment() {
    }

    public String getComentID() {
        return comentID;
    }

    public void setComentID(String comentID) {
        this.comentID = comentID;
    }

    public String getFromtID() {
        return fromtID;
    }

    public void setFromtID(String fromtID) {
        this.fromtID = fromtID;
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

}
