package com.example.myapplication;

import android.graphics.drawable.Drawable;

public class AppObject {
    private  String name,
                    packageName;
    private Drawable image;
    public AppObject(String packageName, String name, Drawable image)
    {this.name = name;
    this.packageName = packageName;
    this.image = image;
    }

    public String getPackageName(){return packageName;}
    public String getName(){return name;}
    public Drawable getImage(){return image;}


    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }}

