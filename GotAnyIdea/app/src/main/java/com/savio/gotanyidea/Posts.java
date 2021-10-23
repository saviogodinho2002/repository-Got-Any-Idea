package com.savio.gotanyidea;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Posts  {
    private String fromID;

    private String fromName;

    private String postID;

    private List<String> tag;
    private long timestamp;
    private String urlPhotoPost;

    private String urlPhotoUser;
    private String postText;

    public Posts() {
        tag = new ArrayList<>();
    }
    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {

        this.tag = tag;
    }


    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }


    public String getUrlPhotoUser() {
        return urlPhotoUser;
    }

    public void setUrlPhotoUser(String urlPhotoUser) {
        this.urlPhotoUser = urlPhotoUser;
    }


    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrlPhotoPost() {
        return urlPhotoPost;
    }

    public void setUrlPhotoPost(String urlPhotoPost) {
        this.urlPhotoPost = urlPhotoPost;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}
