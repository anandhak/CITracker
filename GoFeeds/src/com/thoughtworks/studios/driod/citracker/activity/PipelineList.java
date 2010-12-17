package com.thoughtworks.studios.driod.citracker.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.thoughtworks.studios.driod.citracker.FeedParserFactory;
import com.thoughtworks.studios.driod.citracker.MainMenuOptions;
import com.thoughtworks.studios.driod.citracker.R;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;
import com.thoughtworks.studios.driod.citracker.view.PipelineListAdapter;

import java.util.List;

/**
 * Understands named pipeline and show them with the information from the
 * lastest run. Pass/fail
 */
public class PipelineList extends ListActivity {
	private List<Pipeline> pipelines;
	public static final String SELECTED_PIPELINE_URL_KEY = "com.thoughtworks.studios.driod.citracker.activity.CurrentPipelineName";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		pipelines = FeedParserFactory.loadPipelines(this);
		this.setListAdapter(new PipelineListAdapter(this, R.layout.row, pipelines));
	}

    @Override
    protected void onResume() {
        super.onResume();
        pipelines = FeedParserFactory.loadPipelines(this);
        this.setListAdapter(new PipelineListAdapter(this, R.layout.row, pipelines));
    }

    @Override
	public void onContentChanged() {
		super.onContentChanged();
		pipelines = FeedParserFactory.loadPipelines(this);
		this.setListAdapter(new PipelineListAdapter(this, R.layout.row, pipelines));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MainMenuOptions.ADD_PIPELINE.ordinal(), MainMenuOptions.ADD_PIPELINE.ordinal(),R.string.add_pipeline);
		menu.add(Menu.NONE, MainMenuOptions.PREFERENCES.ordinal(), MainMenuOptions.PREFERENCES.ordinal(),R.string.preferences);
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
		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent myIntent = new Intent();
		myIntent.setClass(getApplicationContext(), MessageList.class);
		myIntent.putExtra(SELECTED_PIPELINE_URL_KEY, pipelines.get(position).getPipelineFeedUrl());
		startActivity(myIntent);
	}

}
