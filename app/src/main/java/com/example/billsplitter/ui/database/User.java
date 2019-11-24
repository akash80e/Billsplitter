package com.example.billsplitter.ui.database;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public String userID;
    public User(){}

    public User(String name, String email, String userID){
        this.name = name;
        this.email = email;
        this.userID = userID;
    }
}
