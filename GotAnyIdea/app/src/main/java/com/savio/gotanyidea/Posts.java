package com.savio.gotanyidea;

import android.os.Parcel;
import android.os.Parcelable;

public class Posts implements Parcelable {
    private String fromID;

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    private String fromName;
    private String Tag;
    private long timestamp;
    private String urlPhotoPost;

    public String getUrlPhotoUser() {
        return urlPhotoUser;
    }

    public void setUrlPhotoUser(String urlPhotoUser) {
        this.urlPhotoUser = urlPhotoUser;
    }

    private String urlPhotoUser;
    private String postText;

    public Posts() {

    }

    protected Posts(Parcel in) {
        fromID = in.readString();
        Tag = in.readString();
        timestamp = in.readLong();
        urlPhotoPost = in.readString();
        postText = in.readString();
        urlPhotoUser = in.readString();
        fromID = in.readString();
    }

    public static final Creator<Posts> CREATOR = new Creator<Posts>() {
        @Override
        public Posts createFromParcel(Parcel in) {
            return new Posts(in);
        }

        @Override
        public Posts[] newArray(int size) {
            return new Posts[size];
        }
    };

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromID);
        dest.writeString(Tag);
        dest.writeLong(timestamp);
        dest.writeString(urlPhotoPost);
        dest.writeString(postText);
        dest.writeString(urlPhotoUser);
        dest.writeString(fromName);
    }
}
