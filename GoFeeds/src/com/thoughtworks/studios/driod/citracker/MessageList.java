package com.thoughtworks.studios.driod.citracker;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.studios.driod.citracker.view.SpecialAdapter;
import org.thoughtworks.android.R;

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

public class MessageList extends ListActivity {
	
	private List<Message> messages;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(org.thoughtworks.android.R.layout.main);
        loadFeed(ParserType.SAX);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, ParserType.ANDROID_SAX.ordinal(), 
				ParserType.ANDROID_SAX.ordinal(), R.string.android_sax);
		menu.add(Menu.NONE, ParserType.SAX.ordinal(), ParserType.SAX.ordinal(),
				R.string.sax);
		menu.add(Menu.NONE, ParserType.DOM.ordinal(), ParserType.DOM.ordinal(), 
				R.string.dom);
		menu.add(Menu.NONE, ParserType.XML_PULL.ordinal(), 
				ParserType.XML_PULL.ordinal(), R.string.pull);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		ParserType type = ParserType.values()[item.getItemId()];
		ArrayAdapter<String> adapter =
			(ArrayAdapter<String>) this.getListAdapter();
		if (adapter.getCount() > 0){
			adapter.clear();
		}
		this.loadFeed(type);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent viewMessage = new Intent(Intent.ACTION_VIEW, 
				Uri.parse(messages.get(position).getLink().toExternalForm()));
		this.startActivity(viewMessage);
	}

	private void loadFeed(ParserType type){
    	try{
    		Log.i("Go Feeds", "ParserType="+type.name());
	    	FeedParser parser = FeedParserFactory.getParser(type);
	    	long start = System.currentTimeMillis();
	    	messages = parser.parse();
	    	long duration = System.currentTimeMillis() - start;
	    	Log.i("Go Feeds", "Parser duration=" + duration);

//            process();

	    	List<String> titles = new ArrayList<String>(messages.size());
	    	List<String> statuses = new ArrayList<String>(messages.size());
	    	for (Message msg : messages){
	    		titles.add(msg.getTitle());
	    		statuses.add(msg.getCategory());
	    	}
	    	ArrayAdapter<String> adapter = 
	    		new SpecialAdapter<String>(this, R.layout.row, titles, statuses);
	    	this.setListAdapter(adapter);
    	} catch (Throwable t){
    		Log.e("Go Feeds",t.getMessage(),t);
    	}
    }

}