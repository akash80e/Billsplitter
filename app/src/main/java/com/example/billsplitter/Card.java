package com.example.billsplitter;

public class Card {
    private int icon;
    private String name;

    public Card(int img, String text){
        icon = img;
        name = text;
    }

    public int getImg(){
        return icon;
    }

    public String getText(){
        return name;
    }
}
