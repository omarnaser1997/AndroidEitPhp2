package com.naser.omar.androideitserverphp.Model;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    public Integer id;
    @SerializedName("name")
    public String name;
    @SerializedName("qty")
    public Double qty;
    @SerializedName("price")
    public Double price;
}