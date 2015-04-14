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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        readItems();
        // Initialize adapter; here simple_list_item_1 is a reference to an built-in XML layout
        // document that is part of the Android OS, rather than one of my own XML layouts
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        // Connect the adapter to listView element
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
//                itemsAdapter.remove(items.get(position));
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) parent.getItemAtPosition(position);
                // first parameter is the context, second is the class of the activity to launch
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("position", position);
                i.putExtra("value", value);
                // REQUEST_CODE will be used to evaluate the result of the second (child) activity
                startActivityForResult(i, REQUEST_CODE); // brings up the second activity
            }
        });
    }

    /**
     * Time to handle the result of the sub-activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract data from result extras
            String name = data.getExtras().getString("value");
            int position = data.getExtras().getInt("position");

            // Update the item value and write it in the data store
            updateTodoItem(name, position);

            // Toast the success of operation
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Update the value of the item, refresh the listView and update the data store
     * @param newValue
     * @param position
     */
    private void updateTodoItem(String newValue, int position) {
        items.set(position, newValue);
        itemsAdapter.notifyDataSetChanged();
        // Update the file
        writeItems();
    }

    /**
     * Initialize items (property) to read the value from a persistent data store
     * Initialize to empty ArrayList if no data store found
     */
    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e){
            items = new ArrayList<>();
        }
    }

    /**
     * Write the list of value in items (property) to the persistant data store
     */
    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e){
            e.printStackTrace();
        }
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
        // Update the listView
        items.add(itemValue);
        itemsAdapter.notifyDataSetChanged();
        // The above two lines are an alternate to the method mentioned in slides. The logic is
        // similar to how deletion is processed.
//        itemsAdapter.add(itemValue);  <-- From the slides
        // Empty the edit text field
        etNewItem.setText("");
        writeItems();
    }
}
