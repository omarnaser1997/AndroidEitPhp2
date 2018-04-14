package com.naser.omar.androideitserverphp.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by OmarNasser on 4/13/2018.
 */

public class AppNotification {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("textNotification")
    private String textNotification;

    @SerializedName("view")
    private int  view;

    @SerializedName("imageURL")
    private String imageURL;


    public AppNotification(int id,int view, String title, String textNotification,String imageURL) {
        this.id = id;
        this.view = view;
        this.title = title;
        this.imageURL = imageURL;
        this.textNotification = textNotification;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextNotification() {
        return textNotification;
    }

    public void setTextNotification(String textNotification) {
        this.textNotification = textNotification;
    }
}
