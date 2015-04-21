package com.codepath.Helper;

import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.util.List;
import com.codepath.model.Item;

public class DatabaseHelper {
    /**
     * Gets a list of all Item objects
     * Equivalent to SELECT * FROM TABLE_NAME
     * @return
     */
    public List<Item> getAll() {
        return new Select()
                .from(Item.class)
                .execute();
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
}
