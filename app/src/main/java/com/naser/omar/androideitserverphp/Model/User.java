package com.naser.omar.androideitserverphp.Model;

/**
 * Created by OmarNasser on 2/8/2018.
 */
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("Name")
    private String Name;
    @SerializedName("Password")
    private String Password;
    @SerializedName("Number")
    private String Number;
    @SerializedName("secureCode")
    private String secureCode;

    private String image;


    public User() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User(String name, String password, String number, String secureCode, String image) {
        Name = name;
        Password = password;
        Number = number;
        this.secureCode = secureCode;
        this.image = image;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String phone) {
        Number = phone;
    }

    public User(String name, String password, String number,String secureCode) {
        Name = name;
        Password = password;
        Number = number;
        this.secureCode=secureCode;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return Password;
    }
}
