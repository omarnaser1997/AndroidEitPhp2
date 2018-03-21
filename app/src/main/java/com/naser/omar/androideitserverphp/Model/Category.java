package com.naser.omar.androideitserverphp.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by OmarNasser on 1/24/2018.
 */

public class Category {
    @SerializedName("Name")
    private String Name;
    @SerializedName("Image")
    private String Image;
    @SerializedName("categoryID")
    private String categoryID;

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    @SerializedName("base64")
    private String base64;

    public Category() {
    }

    public Category(String name, String image,String CategoryID) {
        Name = name;
        Image = image;
        categoryID=CategoryID;
    }



    public void setName(String name) {
        Name = name;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;

    }
    public String getCategoryID() {
        return categoryID;
    }
    public String getImage() {
        return Image;
    }
}
