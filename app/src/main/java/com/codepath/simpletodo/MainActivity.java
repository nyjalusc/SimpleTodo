package com.codepath.simpletodo;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codepath.Helper.DatabaseHelper;
import com.codepath.model.Item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private TodoItemAdapter todoItemsAdapter;
    private static Calendar calendar;
    private ListView lvItems;
    private DatabaseHelper dbHelper;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Wasted lot of time to set the color of actionbar from xml but failed :(
        // Finally gave up and decided to set it programmatically
        // Bad Design: Fix it after figuring out a way to do this through resources
        Resources res = getResources();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(res.getColor(R.color.teal)));
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
        ArrayList<Item> allItems = new ArrayList<Item>();
        allItems.addAll(todoItems);
//        Collections.reverse(allItems);
        todoItemsAdapter = new TodoItemAdapter(this, allItems);

        // Connect the adapter to listView element
        lvItems.setAdapter(todoItemsAdapter);
        setupListViewListener();
    }

    /**
     * OnClickListeners for listView are defined here.
     */
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
     * The child intent performed the edit operation and returned the result.
     * Since the underlying data has changed the listView is reloaded.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // The underlying content of the listview got changed after the update so refresh it
            refreshListView();

            // Toast the success of update operation in main activity
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Reload the adapter
     */
    private void refreshListView() {
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

        if (itemValue.length() == 0) {
            Toast.makeText(this, "No item to add", Toast.LENGTH_SHORT).show();
            // VERY IMPORTANT: Reset the calendar object
            // The user can setup the due-date and never specify a string value before hitting
            // Add button. In such case the calendar should be reset.
            calendar = null;
            return;
        }

        // Create new item and save it in the database
        Item item = new Item(itemValue);
        // Set the Due-date for the item; It is an optional attribute
        if (calendar != null) {
            item.setDueDate(calendar);
        }
        // Important: Reset the calendar object
        calendar = null;
        item.save();

        // Insert the object in adapter
        todoItemsAdapter.add(item);

        // Empty the edit text field once the operation completes
        etNewItem.setText("");
    }

    /**
     * Renders the DatePicker and sets up onClickListener for TimePicker
     * @param view
     */
    public void showDatePicker(View view) {
            final View dialogView = View.inflate(this, R.layout.date_picker, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

            dialogView.findViewById(R.id.ibSaveDate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                    if (calendar == null) {
                        calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                    } else {
                        // This is to handle the case where the user is trying to edit the previously
                        // saved due-date but has not added the item yet by pressing the "Add button"
                        calendar.set(Calendar.YEAR, datePicker.getYear());
                        calendar.set(Calendar.HOUR, datePicker.getMonth());
                        calendar.set(Calendar.MINUTE, datePicker.getDayOfMonth());
                    }
                    // When the "Done" action is performed load the TimePicker
                    showTimePicker(view);
                    // Destroy the view after the operations complete
                    alertDialog.dismiss();
                }
            });
//            alertDialog.setTitle("Set target datetime");
            alertDialog.setView(dialogView);
            alertDialog.show();
    }

    /**
     * This method won't be called without getting showDatePicker being called first
     * @param view
     */
    public void showTimePicker(View view) {
        final View dialogView = View.inflate(this, R.layout.time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.ibSaveTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                calendar.set(Calendar.HOUR, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }
}
