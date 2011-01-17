package com.thoughtworks.studios.driod.citracker.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;

import java.util.HashMap;
import java.util.Map;

public class PipelineListAdapter extends ArrayAdapter<Pipeline> {
    public static Map<String, Integer> COLOUR_STATUS = new HashMap<String, Integer>();

    static {
        COLOUR_STATUS.put("failed", 0XFFCA2626);
        COLOUR_STATUS.put("passed", 0xFF629E26);
        COLOUR_STATUS.put("cancelled", 0xFFCE9C17);
    }

    public PipelineListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = super.getView(position, convertView, parent);
        }
        try {
            Pipeline item = getItem(position);
            ((TextView) convertView).setText(item.toString());
            convertView.setBackgroundColor(COLOUR_STATUS.get(item.getCurrentStatus()));
        } catch (Exception e) {
            //TODO:Dont try to change state of UI on failure. Make toast
            return convertView;
        }
        return convertView;
    }

}