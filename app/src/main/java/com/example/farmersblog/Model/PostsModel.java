package com.example.farmersblog.Model;

public class PostsModel {
    String pTitle, pImage, pDescription;

    public PostsModel() {
    }

    public PostsModel(String pTitle, String pImage, String pDescription) {
        this.pTitle = pTitle;
        this.pImage = pImage;
        this.pDescription = pDescription;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpDescription() {
        return pDescription;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }
}
