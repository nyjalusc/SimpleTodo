package com.codepath.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.util.List;

@Table(name = "Items")
public class Item extends Model {
    // This is a regular field
    @Column(name = "Name", index = true)
    public String name;
    @Column(name = "DueDate", index = true)
    public String dueDate;

    // Make sure to have a default constructor for every ActiveAndroid model
    public Item(){
        super();
    }

//    @Override
//    public String toString() {
//        return name;
//    }

    public Item(String name){
        super();
        this.name = name;
        this.dueDate = new String("2000-01-01");
    }
}
