package com.naser.omar.androideitserverphp.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by OmarNasser on 2/24/2018.
 */

public class Rating {
    @SerializedName("ratingID")
    private String ratingID;
    @SerializedName("user_number")
    private String user_number;// i will use User Phone to the Key of Data
    @SerializedName("foodnID")
    private String foodnID;
    @SerializedName("value")
    private String value;
    @SerializedName("comments")
    private String comments;

    public String getRatingID() {
        return ratingID;
    }

    public void setRatingID(String ratingID) {
        this.ratingID = ratingID;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    public String getFoodnID() {
        return foodnID;
    }

    public void setFoodnID(String foodnID) {
        this.foodnID = foodnID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Rating(String user_number, String foodnID, String value, String comments) {

        this.user_number = user_number;
        this.foodnID = foodnID;
        this.value = value;
        this.comments = comments;
    }

    public Rating() {

    }
}
