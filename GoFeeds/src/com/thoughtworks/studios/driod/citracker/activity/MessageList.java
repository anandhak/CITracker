package com.thoughtworks.studios.driod.citracker.activity;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thoughtworks.studios.driod.citracker.FeedParser;
import com.thoughtworks.studios.driod.citracker.FeedParserFactory;
import com.thoughtworks.studios.driod.citracker.MainMenuOptions;
import com.thoughtworks.studios.driod.citracker.R;
import com.thoughtworks.studios.driod.citracker.model.Message;
import com.thoughtworks.studios.driod.citracker.view.PipelineStatusListAdapter;

public class MessageList extends ListActivity {

    static String feedUrl = "http://go02.thoughtworks.com:8153/go/api/pipelines/cruise/stages.xml";
    
	private List<Message> messages;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(com.thoughtworks.studios.driod.citracker.R.layout.main);

        String currentPipeline = getIntent().getStringExtra(PipelineList.SELECTED_PIPELINE_URL_KEY);
        Log.i("Pipeline runs-- showing", "Pipeline url="+currentPipeline);
        feedUrl = currentPipeline;
        loadFeed();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MainMenuOptions.ADD_PIPELINE.ordinal(),
                MainMenuOptions.ADD_PIPELINE.ordinal(), R.string.app_name);
        menu.add(Menu.NONE, MainMenuOptions.PREFERENCES.ordinal(),
                MainMenuOptions.PREFERENCES.ordinal(), R.string.app_name);

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		ArrayAdapter<String> adapter =
			(ArrayAdapter<String>) this.getListAdapter();
		if (adapter.getCount() > 0){
			adapter.clear();
		}
		this.loadFeed();
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent viewMessage = new Intent(Intent.ACTION_VIEW, 
				Uri.parse(messages.get(position).getLink().toExternalForm()));
		this.startActivity(viewMessage);
	}

	private void loadFeed(){
    	try{
	    	FeedParser parser = FeedParserFactory.getParser(feedUrl);
	    	long start = System.currentTimeMillis();
	    	messages = parser.parse();
	    	long duration = System.currentTimeMillis() - start;
	    	Log.i("Go Feeds", "Parser duration=" + duration);
	    	ArrayAdapter<Message> adapter = 
	    		new PipelineStatusListAdapter(this, R.layout.row, messages);
	    	this.setListAdapter(adapter);
    	} catch (Throwable t){
    		Log.e("Go Feeds",t.getMessage(),t);
    	}
    }

}