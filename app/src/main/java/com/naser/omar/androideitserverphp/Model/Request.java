package com.naser.omar.androideitserverphp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by OmarNasser on 1/29/2018.
 */

public class Request {
    @SerializedName("Phone")
    private String Phone;
    @SerializedName("Name")
    private String Name;
    @SerializedName("Address")
    private String Address;
    @SerializedName("Tooal")
    private String Tooal;
    @SerializedName("Status")
    private String Status;
    @SerializedName("requesteID")
    private String requesteID;
    @SerializedName("comment")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRequesteID() {
        return requesteID;
    }

    public void setRequesteID(String requesteID) {
        this.requesteID = requesteID;
    }

    private List<Order> foods;//list of food order

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getTooal() {
        return Tooal;
    }

    public void setTooal(String tooal) {
        Tooal = tooal;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public Request(String phone, String name, String address, String tooal, List<Order> foods) {
        Phone = phone;
        Name = name;
        Address = address;
        Tooal = tooal;
        this.foods = foods;
    }

    public Request() {

    }


}
