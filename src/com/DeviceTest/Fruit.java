package com.DeviceTest;

public class Fruit {
    private int imageId;
    private String name;


    public  Fruit(String name,int imageId) {
    	this.name=name;
        this.imageId=imageId;
    }

    public int getImageId() {
        return imageId;
    }
     public String getName() {
        return name;
    }
}
