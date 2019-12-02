package com.example.billsplitter;

import java.util.List;

public class JSONobject {

    public String type;
    public List<ContactInfo> value;

    public JSONobject(String type, List<ContactInfo> value) {
        this.type = type;
        this.value = value;
    }

    public JSONobject() {
        super();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ContactInfo> getValue() {
        return value;
    }

    public void setValue(List<ContactInfo> value) {
        this.value = value;
    }
}
