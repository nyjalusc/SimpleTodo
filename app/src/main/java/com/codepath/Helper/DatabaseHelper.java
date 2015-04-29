package com.codepath.Helper;

import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.codepath.model.Item;

import java.util.GregorianCalendar;
import java.util.List;

public class DatabaseHelper {
    /**
     * Gets a list of all Item objects
     * Equivalent to SELECT * FROM TABLE_NAME
     * @return
     */
    public List<Item> getAll() {
        List<Item> items = new Select()
                .from(Item.class)
                .orderBy("Id DESC")
                .execute();
        return items;
    }

    /**
     * Empties the table
     */
    public void deleteAll()
    {
        SQLiteUtils.execSql("DELETE FROM Items");
    }

    /**
     * Here "Id" is an auto-generated column which is used as a primary key.
     * @param itemId
     * @return
     */
    public Item getItem(long itemId) {
        return new Select()
                .from(Item.class)
                .where("Id = ?", itemId).executeSingle();
    }

    /**
     * Performs a "LIKE" query. Used in search widget.
     * @param searchTerm
     * @return
     */
    public List<Item> searchItem(String searchTerm) {
        String likeQueryTerm = '%' + searchTerm + '%';
        List<Item> items = new Select()
                .from(Item.class)
                .where("Name LIKE ?", likeQueryTerm)
                .orderBy("Id DESC")
                .execute();
        return items;
    }

    public List<Item> getExpiredItems() {
        return new Select()
                .from(Item.class)
                .where("DueDate < ?", new GregorianCalendar().getTimeInMillis())
                .execute();
    }
}
