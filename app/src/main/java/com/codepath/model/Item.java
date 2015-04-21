package com.codepath.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Table(name = "Items")
public class Item extends Model {
    // This is a regular field
    @Column(name = "Name", index = true)
    public String name;
    @Column(name = "DueDate", index = true)
    private Date dueDate;

    // Make sure to have a default constructor for every ActiveAndroid model
    public Item(){
        super();
    }

    public Item(String name){
        super();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDueDate(Calendar calendar) {
        this.dueDate = calendar.getTime();
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getFormattedDueDate() {
        return getFormattedDueDate("MMM dd HH:mm");
    }

    public String getFormattedDueDate(String format){
        if (this.dueDate == null) {
            return "";
        }
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(this.dueDate);
    }
}
