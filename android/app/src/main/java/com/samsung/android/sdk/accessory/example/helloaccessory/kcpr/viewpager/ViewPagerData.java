package com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.viewpager;

public class ViewPagerData {
    int image;
    String description;
//    String button_text;

    public ViewPagerData(int color, String title){
        this.image = color;
        this.description = title;
//        this.button_text = null;
    }

//    public ViewPagerData(int color, String title, String button_text){
//        this.image = color;
//        this.description = title;
//        this.button_text = button_text;
//    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getButton_text() {
//        return button_text;
//    }
//
//    public void setButton_text(String button_text) {
//        this.button_text = button_text;
//    }
}
