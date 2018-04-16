package com.naser.omar.androideitserverphp.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by OmarNasser on 4/16/2018.
 */

public class ViewNotification {

    @SerializedName("view")
    int view;

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public ViewNotification(int view) {
        this.view = view;
    }
}
