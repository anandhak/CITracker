package com.thoughtworks.studios.driod.citracker.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.thoughtworks.studios.driod.citracker.FeedParserFactory;
import com.thoughtworks.studios.driod.citracker.MainMenuOptions;
import com.thoughtworks.studios.driod.citracker.R;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;
import com.thoughtworks.studios.driod.citracker.view.PipelineListAdapter;

/**
 * Understands named pipeline and show them with the information from the
 * lastest run. Pass/fail
 */
public class PipelineList extends ListActivity {
	private List<Pipeline> pipelines;

	public static String feedUrl = "http://go02.thoughtworks.com:8153/go/api/pipelines/%s/stages.xml";
	public static final String PIPELINE_1 = "cruise";
	public static final String PIPELINE_2 = "acceptance";
	public static final String SELECTED_PIPELINE_URL_KEY = "com.thoughtworks.studios.driod.citracker.activity.CurrentPipelineName";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(com.thoughtworks.studios.driod.citracker.R.layout.main);
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
