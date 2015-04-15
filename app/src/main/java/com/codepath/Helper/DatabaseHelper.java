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
     * Use LIMIT (n, 1) to setup a correlation between the listview row and the DB object
     * Returns Nth row from the 1st row in table
     * @param position
     * @return
     */
    public Item getItem(int position) {
        Integer pos = new Integer(position);
        return SQLiteUtils.rawQuerySingle(Item.class,
                "SELECT * from Items LIMIT ?, 1",
                new String[] {pos.toString()}
        );
    }
}
