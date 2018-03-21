package com.naser.omar.androideitserverphp.Model;

import android.media.Image;

import com.google.gson.annotations.SerializedName;

/**
 * Created by OmarNasser on 1/25/2018.
 */

public class Food {
    @SerializedName("FoodID")
    private String FoodID;
    @SerializedName("Name")
    private String Name;
    @SerializedName("Image")
    private String Image;
    @SerializedName("Description")
    private String Description;
    @SerializedName("Price")
    private String Price;
    @SerializedName("Discount")
    private String  Discount;
    @SerializedName("FK_CategoryID")
    private String FK_CategoryID;
    @SerializedName("base64")
    private String base64;

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodID) {
        FoodID = foodID;
    }

    public String getFK_CategoryID() {
        return FK_CategoryID;
    }

    public void setFK_CategoryID(String FK_CategoryID) {
        this.FK_CategoryID = FK_CategoryID;
    }

    public Food(String foodID, String name, String image, String description, String price, String discount, String FK_CategoryID) {
        FoodID = foodID;
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        this.FK_CategoryID = FK_CategoryID;
    }

    public Food() {
    }

//    public Food(String name, String image, String description, String price, String discount, String fK_CategoryID) {
//        Name = name;
//        Image = image;
//        Description = description;
//        Price = price;
//        Discount = discount;
//        FK_CategoryID = fK_CategoryID;
//    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getMenuId() {
        return FK_CategoryID;
    }

    public void setMenuId(String fK_CategoryID) {
        FK_CategoryID = fK_CategoryID;
    }
}
