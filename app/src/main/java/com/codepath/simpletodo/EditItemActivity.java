package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class EditItemActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        populateFields();
    }

    /**
     * Populate the EditText field in current Activity by using the data passed by parent Activity
     */
    private void populateFields() {
        // Extract the values passed from parent activity
        String value = getIntent().getStringExtra("currentValue");

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
     * When the "Save" button is clicked the new value from the edit activity has to be passed
     * to the parent activity to update the listView.
     * @param view
     */
    public void onSave(View view) {
        // This is to preserve the position of the element that got clicked
        // It will be used to update the correct element from ArrayList in parent activity
        int position = getIntent().getIntExtra("position", 0);

        EditText etItem  = (EditText) findViewById(R.id.etTextField);
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("newItemValue", etItem.getText().toString());
        data.putExtra("position", position);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
