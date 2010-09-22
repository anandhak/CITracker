package com.thoughtworks.studios.driod.citracker.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialAdapter<T> extends ArrayAdapter<T> {
    private static Map<String, Integer> colourStatus = new HashMap<String, Integer>();
    static{
        colourStatus.put("failed", Color.RED);
        colourStatus.put("passed", Color.GREEN);
        colourStatus.put("cancelled", Color.YELLOW);
    };
    private List<String> statuses;

    public SpecialAdapter(Context context, int textViewResourceId, List<T> titles, List<String> statuses) {
        super(context, textViewResourceId, titles);
        this.statuses = statuses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundColor(colourStatus.get(statuses.get(position)));
        return view;
    }
}