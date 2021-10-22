package com.savio.gotanyidea;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String name;
    private String userID;
    private String urlProfilePhoto;
    private String token;

    public User() {

    }

    protected User(Parcel in) {
        name = in.readString();
        userID = in.readString();
        urlProfilePhoto = in.readString();
        token = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUrlProfilePhoto() {
        return urlProfilePhoto;
    }

    public void setUrlProfilePhoto(String urlProfilePhoto) {
        this.urlProfilePhoto = urlProfilePhoto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(userID);
        dest.writeString(urlProfilePhoto);
        dest.writeString(token);
    }
}
