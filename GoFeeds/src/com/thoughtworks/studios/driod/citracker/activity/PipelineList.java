package com.thoughtworks.studios.driod.citracker.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.thoughtworks.studios.driod.citracker.FeedParser;
import com.thoughtworks.studios.driod.citracker.FeedParserFactory;
import com.thoughtworks.studios.driod.citracker.MainMenuOptions;
import com.thoughtworks.studios.driod.citracker.R;
import com.thoughtworks.studios.driod.citracker.model.Message;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;
import com.thoughtworks.studios.driod.citracker.view.PipelineListAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Understands named pipeline and show them with the information from the lastest run. Pass/fail
 */
public class PipelineList extends ListActivity {
    private List<Pipeline> pipelines;

    static String feedUrl = "http://go02.thoughtworks.com:8153/go/api/pipelines/%s/stages.xml";
    private static final String PIPELINE_1 = "cruise";
    private static final String PIPELINE_2 = "acceptance";
    private static final String PIPELINE_3 = "acceptance-ie";
    public static final String SELECTED_PIPELINE_URL_KEY = "com.thoughtworks.studios.driod.citracker.activity.CurrentPipelineName";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(com.thoughtworks.studios.driod.citracker.R.layout.main);
        pipelines = new ArrayList<Pipeline>();
        loadPipelines();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MainMenuOptions.ADD_PIPELINE.ordinal(),
                MainMenuOptions.ADD_PIPELINE.ordinal(), com.thoughtworks.studios.driod.citracker.R.string.add_pipeline);
        menu.add(Menu.NONE, MainMenuOptions.PREFERENCES.ordinal(),
                MainMenuOptions.PREFERENCES.ordinal(), com.thoughtworks.studios.driod.citracker.R.string.preferences);
        return true;
    }
    

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        MainMenuOptions option = MainMenuOptions.values()[item.getItemId()];
        if(option == MainMenuOptions.PREFERENCES) {
        	Log.i("On menu selection" + option.toString(), item.toString());
            Intent myIntent = new Intent();
            myIntent.setClass(getApplicationContext(),PreferencesFromCode.class);
            startActivity(myIntent);    
        }
        Toast.makeText(getApplicationContext(), "Picking Pref", Toast.LENGTH_LONG);
        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent myIntent = new Intent();
        myIntent.setClass(getApplicationContext(),MessageList.class);
        myIntent.putExtra(SELECTED_PIPELINE_URL_KEY, pipelines.get(position).getPipelineFeedUrl()); // key/value pair, where key needs current package prefix.
        startActivity(myIntent);    
    }

    private void loadPipelines() {
        try {
            pipelines.add(loadSinglePipeline(PIPELINE_1, String.format(feedUrl,PIPELINE_1)));
            pipelines.add(loadSinglePipeline(PIPELINE_2, String.format(feedUrl,PIPELINE_2)));
//            pipelines.add(loadSinglePipeline(PIPELINE_3, String.format(feedUrl,PIPELINE_3)));
            ArrayAdapter<Pipeline> adapter =
                    new PipelineListAdapter(this, R.layout.row, pipelines);
            this.setListAdapter(adapter);
        } catch (Throwable t) {
            Log.e("Go Feeds", t.getMessage(), t);
        }
    }

    private Pipeline loadSinglePipeline(String pipelineName, String feedUrl) {
        Log.i("Pipeline List", "loading feeds" + feedUrl);
        FeedParser parser = FeedParserFactory.getParser(feedUrl);
        long start = System.currentTimeMillis();
        List<Message> messages = parser.parse();
        long duration = System.currentTimeMillis() - start;
        Log.i("Go Feeds", "Parser duration=" + duration);
        return new Pipeline(messages, pipelineName, feedUrl);
    }

}
