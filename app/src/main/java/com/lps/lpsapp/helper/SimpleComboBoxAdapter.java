package com.lps.lpsapp.helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dle on 03.12.2015.
 */
public class SimpleComboBoxAdapter extends ArrayAdapter<ComboBoxItem>
{
    // Your custom values for the spinner (User)
    private List<ComboBoxItem> values;

    public SimpleComboBoxAdapter(Context context, List<ComboBoxItem> values) {
        super(context, android.R.layout.simple_spinner_item,values);
        this.values = values;
    }

    public int getCount(){
        return values.size();
    }

    public ComboBoxItem getItem(int position){
        return values.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position,convertView,parent);
        ((TextView)convertView).setText(values.get(position).text);

        // And finally return your dynamic (or custom) view for each spinner item
        return convertView;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        convertView = super.getDropDownView(position, convertView, parent);
        ((TextView)convertView).setText(values.get(position).text);

        // And finally return your dynamic (or custom) view for each spinner item
        return convertView;
    }

}
