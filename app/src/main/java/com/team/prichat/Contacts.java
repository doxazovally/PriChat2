package com.team.prichat;

public class Contacts {

    private String mName, mStatus, mImages;

    public Contacts(){

    }

    public Contacts(String name, String status, String Images) {
        mName = name;
        mStatus = status;
        mImages = Images;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getImages() {
        return mImages;
    }

    public void setImages(String Images) {
        mImages = Images;
    }
}
