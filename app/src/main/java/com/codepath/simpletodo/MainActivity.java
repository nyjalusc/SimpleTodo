package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.Helper.DatabaseHelper;
import com.codepath.model.Item;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private TodoItemAdapter todoItemsAdapter;
    private ListView lvItems;
    private DatabaseHelper dbHelper;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    /**
     * Initialize the properties
     */
    private void init() {
        dbHelper = new DatabaseHelper();
        lvItems = (ListView) findViewById(R.id.lvItems);
        List<Item> todoItems = readItems();
        // Initialize adapter; here simple_list_item_1 is a reference to an built-in XML layout
        // document that is part of the Android OS, rather than one of my own XML layouts
//        todoItemsAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, todoItems);
        ArrayList<Item> atodoItems = new ArrayList<Item>();
        atodoItems.addAll(todoItems);
        todoItemsAdapter = new TodoItemAdapter(this, atodoItems);

        // Connect the adapter to listView element
        lvItems.setAdapter(todoItemsAdapter);
        setupListViewListener();
    }

    private void setupListViewListener() {

        // DELETION LOGIC
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                // Fade-out animation effect
                view.animate().setDuration(1000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                deleteSingleItemAndRefreshView(position);
                                view.setAlpha(1);
                            }
                        });
//                deleteSingleItemAndRefreshView(position);
                // Toast the success of update operation
                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // UPDATE LOGIC
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // NOTE: Casting - (String) parent.getItemAtPosition(position) will fail
                // Explicitly invoke the toString() over the Item object
                Item clickedItem = (Item) parent.getItemAtPosition(position);
                String currentItemValue = clickedItem.name;

                // first parameter is the context, second is the class of the activity to launch
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("position", position);
                i.putExtra("currentValue", currentItemValue);

                // REQUEST_CODE will be used to evaluate the result of the second (child) activity
                startActivityForResult(i, REQUEST_CODE); // brings up the second activity
            }
        });
    }

    /**
     * Delete the Item object and update the adapter to refresh the view
     * @param position
     */
    protected void deleteSingleItemAndRefreshView(int position) {
        Item removeItem = dbHelper.getItem(position);
        todoItemsAdapter.remove(removeItem);
        removeItem.delete();
    }

    /**
     * Time to handle the result of the sub-activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract data from result extras
            String newItemValue = data.getExtras().getString("newItemValue");
            int position = data.getExtras().getInt("position");

            // Get the todoItem at position
            Item updateItem = getTodoItemAtPosition(position);

            // Update the item value and write it in the data store
            updateTodoItem(updateItem, newItemValue, position);

            // Toast the success of update operation
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Returns Item object from the db
     * Here position is between [0..(NUM_OF_ROWS_IN_DB - 1)]
     * @param position
     * @return
     */
    private Item getTodoItemAtPosition(int position) {
        return dbHelper.getItem(position);
    }

    /**
     * Update the value of the item and reload the adapter
     * @param newValue
     * @param position
     */
    private void updateTodoItem(Item updateItem, String newValue, int position) {
        updateItem.name = newValue;
        updateItem.save();
        // tried to use insert() to only update the required object but it did not work
        // This looks like an expensive operation but couldn't figure out a clean way to update
        // the adapter.
        todoItemsAdapter.clear();
        todoItemsAdapter.addAll(dbHelper.getAll());
        todoItemsAdapter.notifyDataSetChanged();
    }

    /**
     * Fetch rows from db to restore the state of application
     */
    private List<Item> readItems() {
        List<Item> todoItems = dbHelper.getAll();
        return todoItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle addition of new item to the list
     * Gets called when "Add Item" button is clicked
     * @param view
     */
    public void onAddItem(View view) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemValue = etNewItem.getText().toString();

        // Create new item and save it in the database
        Item item = new Item(itemValue);
        item.save();

        // Insert the object in adapter
        todoItemsAdapter.add(item);

        // Empty the edit text field once the operation completes
        etNewItem.setText("");
    }
}
