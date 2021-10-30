package com.savio.gotanyidea;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Posts  implements Parcelable{
    private String fromID;
    private String postID;
    private String photoPostFileName;
    private List<String> tag;
    private long timestamp;
    private String urlPhotoPost;
    private String postText;
    private int numLikes;
    private List<String> userLikedId;

    public Posts() {
        tag = new ArrayList<>();
        userLikedId = new ArrayList<>();
    }

    protected Posts(Parcel in) {
        fromID = in.readString();

        postID = in.readString();
        photoPostFileName = in.readString();
        tag = in.createStringArrayList();
        timestamp = in.readLong();
        urlPhotoPost = in.readString();

        postText = in.readString();
        numLikes = in.readInt();
        userLikedId = in.createStringArrayList();
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

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public List<String> getUserLikedId() {
        return userLikedId;
    }

    public void setUserLikedId(List<String> userLikedId) {
        this.userLikedId = userLikedId;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {

        this.tag = tag;
    }



    public String getPhotoPostFileName() {
        return photoPostFileName;
    }

    public void setPhotoPostFileName(String photoPostFileName) {
        this.photoPostFileName = photoPostFileName;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromID);

        dest.writeString(postID);
        dest.writeString(photoPostFileName);
        dest.writeStringList(tag);
        dest.writeLong(timestamp);
        dest.writeString(urlPhotoPost);

        dest.writeString(postText);
        dest.writeInt(numLikes);
        dest.writeStringList(userLikedId);
    }
}
