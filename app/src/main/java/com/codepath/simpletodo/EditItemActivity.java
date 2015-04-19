package com.codepath.simpletodo;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.codepath.Helper.DatabaseHelper;
import com.codepath.model.Item;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class EditItemActivity extends ActionBarActivity {
    private Calendar calendar;
    private Item updateItem;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Wasted lot of time to set the color of actionbar from xml but failed :(
        // Finally gave up and decided to set it programmatically
        // Bad Design: Fix it after figuring out a way to do this through resources
        Resources res = getResources();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(res.getColor(R.color.teal)));

        dbHelper = new DatabaseHelper();
        populateFields();
    }

    /**
     * Populate the EditText field in current Activity by using the data passed by parent Activity
     */
    private void populateFields() {
        // Extract the values passed from parent activity
        String value = getIntent().getStringExtra("currentValue");
        int position = getIntent().getIntExtra("position", 0);
        // Get the original item object from the db which is going to be edited
        updateItem = getTodoItemAtPosition(position);

        // Get the editText element
        EditText etEditTextField = (EditText) findViewById(R.id.etTextField);
        // Set the value of the editText field
        etEditTextField.setText("");
        // This is to move the cursor at the end
        etEditTextField.append(value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
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
     * The updated item is stored in the db when "Save" button is clicked.
     * No need to pass any data back from the intent to the parent intent.
     * @param view
     */
    public void onSave(View view) {
        EditText etItem  = (EditText) findViewById(R.id.etTextField);
        // Get the new value of item from editText
        String newValue = etItem.getText().toString();
        // Update Item name
        updateItem.name = newValue;

        // If calendar is not null, it means that the dueDate was edited
        if (calendar != null) {
            updateItem.setDueDate(calendar);
        }

        // Save the new update Item object
        updateItem.save();

        setResult(RESULT_OK, null); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
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
     * Loads the datePicker widget in the alertDialog
     * @param view
     */
    public void showDatePicker(View view) {
        final View dialogView = View.inflate(this, R.layout.date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.ibSaveDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                // The line below failed because this piece of code gets executed in asynchronous manner
                // Causes undesired behavior due to unknown reasons. It is better to work on calendar property, write to the
                // DB at the very end when the user hits "Done" button.
//                updateItem.setDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                showTimePicker(view);
                alertDialog.dismiss();
            }

        });
        alertDialog.setTitle("Update datetime");
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    /**
     * Gets loaded from the DatePicker alertDialog.
     * Updates
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
                // Check the comment in showDatePicker method to read about why this line wouldn't work.
//                updateItem.setTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }
}
