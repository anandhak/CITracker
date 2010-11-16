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

import com.thoughtworks.studios.driod.citracker.FeedParserFactory;
import com.thoughtworks.studios.driod.citracker.MainMenuOptions;
import com.thoughtworks.studios.driod.citracker.R;
import com.thoughtworks.studios.driod.citracker.model.Message;
import com.thoughtworks.studios.driod.citracker.view.PipelineStatusListAdapter;

public class MessageList extends ListActivity {

	private List<Message> messages;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(com.thoughtworks.studios.driod.citracker.R.layout.main);

		String currentPipeline = getIntent().getStringExtra(PipelineList.SELECTED_PIPELINE_URL_KEY);
		Log.i("Pipeline runs-- showing", "Pipeline url=" + currentPipeline);
		messages = FeedParserFactory.loadFeed(currentPipeline, FeedParserFactory.getAuthString(this));
		ArrayAdapter<Message> adapter = new PipelineStatusListAdapter(this, R.layout.row, messages);
		this.setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MainMenuOptions.ADD_PIPELINE.ordinal(), MainMenuOptions.ADD_PIPELINE.ordinal(),
				com.thoughtworks.studios.driod.citracker.R.string.add_pipeline);
		menu.add(Menu.NONE, MainMenuOptions.PREFERENCES.ordinal(), MainMenuOptions.PREFERENCES.ordinal(),
				com.thoughtworks.studios.driod.citracker.R.string.preferences);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		MainMenuOptions option = MainMenuOptions.values()[item.getItemId()];
		if (option == MainMenuOptions.PREFERENCES) {
			Log.i("On menu selection" + option.toString(), item.toString());
			Intent myIntent = new Intent();
			myIntent.setClass(getApplicationContext(), PreferencesFromCode.class);
			startActivity(myIntent);
		}
		reloadMessages();
		return false;
	}

	private void reloadMessages() {
		PipelineStatusListAdapter adapter = (PipelineStatusListAdapter) this.getListAdapter();
		if (adapter.getCount() > 0) {
			adapter.clear();
		}
		String currentPipeline = getIntent().getStringExtra(PipelineList.SELECTED_PIPELINE_URL_KEY);
		messages = FeedParserFactory.loadFeed(currentPipeline, FeedParserFactory.getAuthString(this));
		adapter = new PipelineStatusListAdapter(this, R.layout.row, messages);
		this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i("On Browser",String.format("On stage selection navigate to %s on browser!", messages.get(position).getLink()));
		Intent viewMessage = new Intent(Intent.ACTION_VIEW, Uri.parse(messages.get(position).getLink().toExternalForm()));
		this.startActivity(viewMessage);
	}

}