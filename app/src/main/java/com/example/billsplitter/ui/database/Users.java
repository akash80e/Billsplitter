package com.example.billsplitter.ui.database;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Users {

    public String name;
    public Long expenses;

    public Users(){}

    public Users(String name, Long expenses){
        this.name = name;
        this.expenses = expenses;
    }

    public String getUserName(){
        return this.name;
    }

    public Long getUserExpenses(){
        return this.expenses;
    }
}
