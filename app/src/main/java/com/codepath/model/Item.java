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

//    @Override
//    public String toString() {
//        return name;
//    }

    public Item(String name){
        super();
        this.name = name;
//        setDueDateFromString("Jan 30 12:00");
        //setDueDateFromString("2000-01-01 12:00");
    }

    public void setDueDate(Calendar calendar) {
        this.dueDate = calendar.getTime();
    }

    public void setDate(int year, int month, int day) {
        if (this.dueDate == null) {
            Calendar calendar = new GregorianCalendar(year, month, day);
            this.dueDate = calendar.getTime();
        } else {
            // Get the date object and change only the fields which have changed.
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(this.dueDate);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            this.dueDate = calendar.getTime();
        }
    }

    public void setTime(int hour, int minutes) {
        if (this.dueDate == null) {
            // If only setting time and not the date; it should consider current date
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, hour);
            calendar.set(Calendar.MINUTE, minutes);
            this.dueDate = calendar.getTime();
        } else {
            // Get the date object and change only the fields which have changed.
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(this.dueDate);
            calendar.set(Calendar.HOUR, hour);
            calendar.set(Calendar.MINUTE, minutes);
            this.dueDate = calendar.getTime();
        }
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
