package com.codepath.simpletodo;

import android.content.Context;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.codepath.model.Item;

import java.util.Date;
import java.util.ArrayList;

public class TodoItemAdapter extends ArrayAdapter<Item> {
    // View lookup cache
    private static class ViewHolder {
        TextView todoItemName;
        TextView dueDate;
        ImageView letterLabel;
    }

    public TodoItemAdapter(Context context, ArrayList<Item> items) {
        super(context, R.layout.custom_row, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Item item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_row, parent, false);
            viewHolder.todoItemName = (TextView) convertView.findViewById(R.id.tvtodoItemName);
            viewHolder.dueDate = (TextView) convertView.findViewById(R.id.tvDueDate);
            viewHolder.letterLabel = (ImageView) convertView.findViewById(R.id.ivLabel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Color the row if item's due-date has gone past the current dateTime
        if ((item.getDueDate() != null) && (item.getDueDate().compareTo(new Date()) < 0)) {
            convertView.setBackgroundResource(R.color.pink);
        } else {
            convertView.setBackgroundColor(0);
        }

        // Get the drawable letter label
        TextDrawable drawable = getDrawable(item);

        // Populate the data into the template view using the data object
        viewHolder.todoItemName.setText(item.name);
        // Make the TextView scrollable to fit long lines
//        viewHolder.todoItemName.setMovementMethod(new ScrollingMovementMethod());

        // Show the due-date only if it was set for the item. It is an optional attribute
        if (item.getFormattedDueDate().isEmpty()) {
            viewHolder.dueDate.setText("");
        } else {
            viewHolder.dueDate.setText("Due date: " + item.getFormattedDueDate());
        }
        viewHolder.letterLabel.setImageDrawable(drawable);
        // Return the completed view to render on screen
        return convertView;
    }

    private TextDrawable getDrawable(Item item) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate color based on a key (same key returns the same color), useful for list/grid views
        int color = generator.getColor(item.name);

        // declare the builder object once.
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(5)
                .endConfig()
                .round();

        // reuse the builder specs to create multiple drawables
        String firstLetterStr = "" + item.name.toUpperCase().charAt(0); //Convert char to string
        return builder.build(firstLetterStr, color);
    }

}
