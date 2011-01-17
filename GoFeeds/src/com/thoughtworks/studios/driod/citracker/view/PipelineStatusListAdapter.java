package com.thoughtworks.studios.driod.citracker.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.thoughtworks.studios.driod.citracker.model.Message;

import java.util.List;

public class PipelineStatusListAdapter extends ArrayAdapter<Message> {

    public PipelineStatusListAdapter(Context context, int textViewResourceId, List<Message> messages) {
        super(context, textViewResourceId, messages);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
        	convertView = super.getView(position, convertView, parent);
        }
        Message item = getItem(position);
        convertView.setFocusable(false);
        convertView.setBackgroundColor(PipelineListAdapter.COLOUR_STATUS.get(item.getCategory()));
		return convertView;
    }
}