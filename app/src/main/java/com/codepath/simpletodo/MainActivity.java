package com.codepath.simpletodo;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private TodoItemAdapter todoItemsAdapter;
    private Resources res;
    private static Calendar calendar;
    private ListView lvItems;
    private DatabaseHelper dbHelper;
    private final int REQUEST_CODE = 20;
    private android.support.v7.app.ActionBar actionBar;
    private View rootView;

    private float mActionBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Wasted lot of time to set the color of actionbar from xml but failed :(
        // Finally gave up and decided to set it programmatically
        // Bad Design: Fix it after figuring out a way to do this through resources
        res = getResources();
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(res.getColor(R.color.teal)));
//        actionBar.setHideOnContentScrollEnabled(true);

        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.setPadding(0, actionBar.getHeight(), 0, 0);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }

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
                // Use getTag to get the clicked ViewHolder object defined in TodoItemAdapter class
                Object clickedObject = view.getTag();
                final long itemId = ((TodoItemAdapter.ViewHolder) clickedObject).itemId;

                // Fade-out animation effect
                view.animate().setDuration(1000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                deleteSingleItemAndRefreshView(itemId);
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
                /**
                 * NOTE: ListView recycled the "position" attribute hence you cannot rely on that attribute
                 * to find the correct object which got clicked.
                 */
                // Use getTag to get the clicked ViewHolder object defined in TodoItemAdapter class
                Object clickedObject = view.getTag();
                long itemId = ((TodoItemAdapter.ViewHolder) clickedObject).itemId;

                // first parameter is the context, second is the class of the activity to launch
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("itemId", itemId);

                // REQUEST_CODE will be used to evaluate the result of the second (child) activity
                startActivityForResult(i, REQUEST_CODE); // brings up the second activity
            }
        });


        // SHOW / HIDE Actionbar on scrolling
        lvItems.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // NOTE: The following commented out code causing flickering when the actionbar is
                // made to hide or show :(
//                if (view.getId() == lvItems.getId()) {
//                    final int currentFirstVisibleItem = lvItems.getFirstVisiblePosition();
//
//                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
//                        // getSherlockActivity().getSupportActionBar().hide();
////                        getSupportActionBar().hide();
//                        actionBar.hide();
//                        rootView.setPadding(0, 0, 0, 0);
//                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
//                        // getSherlockActivity().getSupportActionBar().show();
//
//                        float scale = getResources().getDisplayMetrics().density;
//                        int dpAsPixels = (int) (16 *scale + 0.5f);
//
////                        getSupportActionBar().show();
//                        actionBar.show();
//                        rootView.setPadding(0, 0, 0, 0);
//                    }
//
//                    mLastFirstVisibleItem = currentFirstVisibleItem;
//                }
            }
        });
    }

    /**
     * Delete the Item object and update the adapter to refresh the view
     * @param itemId
     */
    protected void deleteSingleItemAndRefreshView(long itemId) {
        Item removeItem = dbHelper.getItem(itemId);
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
        List<Item> allItems = dbHelper.getAll();
        todoItemsAdapter.addAll(allItems);
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
        // Obtain the searchView
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Couldn't set the hint using XML :(
        searchView.setQueryHint(res.getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchTerm) {
//                todoItemsAdapter.getFilter().filter(newText);

                // DB operations are costly, try to use the getFilter() method instead
                if (!searchTerm.isEmpty()) {
                    List<Item> searchItems = dbHelper.searchItem(searchTerm);
                    todoItemsAdapter.clear();
                    todoItemsAdapter.addAll(searchItems);
                    todoItemsAdapter.notifyDataSetChanged();
                } else {
                    // Restore the adapter if searchTerm is empty
                    refreshListView();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_list) {
            clearList();
            return true;
        }

        switch (item.getItemId()) {
            case R.id.clear_list:
                clearList();
                return true;

            case R.id.clear_expired:
                clearExpired();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Deletes expired items
     */
    private void clearExpired() {
        List<Item> expiredItems = dbHelper.getExpiredItems();
        for (Iterator<Item> i = expiredItems.iterator(); i.hasNext();) {
            Item expiredItem = i.next();
            todoItemsAdapter.remove(expiredItem);
            expiredItem.delete();
        }
        Toast.makeText(this, "Expired items deleted", Toast.LENGTH_SHORT).show();
    }

    /**
     * Asks for users confirmation before deleting all items
     */
    private void clearList() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Clear list");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to delete all items?")
                .setCancelable(true)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Toast.makeText(MainActivity.this, "Say goodbye to all your data!", Toast.LENGTH_SHORT).show();
                        // if this button is clicked, delete all items
                        dbHelper.deleteAll();
                        refreshListView();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Toast.makeText(MainActivity.this, "Phew! that was close!", Toast.LENGTH_SHORT).show();
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
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

        // Insert the object in adapter at index 0 (first position in the list)
        todoItemsAdapter.insert(item, 0);

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
