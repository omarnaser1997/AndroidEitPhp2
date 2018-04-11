package com.naser.omar.androideitserverphp.Model;

/**
 * Created by OmarNasser on 3/23/2018.
 */

public class Image64 {

    public String name;
    public String image64bit;
    public String id;

    public Image64(String name, String image64bit) {
        this.name = name;
        this.image64bit = image64bit;
    }

    public Image64(String name, String image64bit, String id) {
        this.name = name;
        this.image64bit = image64bit;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage64bit() {
        return image64bit;
    }

    public void setImage64bit(String image64bit) {
        this.image64bit = image64bit;
    }
}
