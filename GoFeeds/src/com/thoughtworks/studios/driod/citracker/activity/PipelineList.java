package com.thoughtworks.studios.driod.citracker.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.thoughtworks.studios.driod.citracker.FeedParser;
import com.thoughtworks.studios.driod.citracker.FeedParserFactory;
import com.thoughtworks.studios.driod.citracker.MainMenuOptions;
import com.thoughtworks.studios.driod.citracker.R;
import com.thoughtworks.studios.driod.citracker.dialogs.DialogsForPipelineActivity;
import com.thoughtworks.studios.driod.citracker.model.Message;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;
import com.thoughtworks.studios.driod.citracker.view.PipelineListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Understands named pipeline and show them with the information from the
 * lastest run. Pass/fail
 */
public class PipelineList extends ListActivity {
    public static final String SELECTED_PIPELINE_URL_KEY = "com.thoughtworks.studios.driod.citracker.activity.CurrentPipelineName";
    private static final String STAGE_FEED_PATH_FORMAT = "/go/api/pipelines/%s/stages.xml";
    private PipelineListAdapter pipelineListAdapter;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        pipelineListAdapter = new PipelineListAdapter(this, R.layout.row);
        this.setListAdapter(pipelineListAdapter);
        loadPipelines();
        pipelineListAdapter.notifyDataSetChanged();
    }

    private void loadPipelines() {
        Context applicationContext = getApplicationContext();
        List<String> pipelineNames = FeedParserFactory.getPipelineString(applicationContext);
        for (String name : pipelineNames) {
            new LoadPipelineFeedTask().execute(name);
        }

    }

    private class LoadPipelineFeedTask extends AsyncTask<String, String, Pipeline> {

        private Pipeline loadSinglePipeline(String pipelineName) {
            Context applicationContext = PipelineList.this.getApplicationContext();
            String serverString = FeedParserFactory.getServerString(applicationContext);
            String serverUrl = String.format(serverString + STAGE_FEED_PATH_FORMAT, pipelineName);
            String authString = FeedParserFactory.getAuthString(applicationContext);
            publishProgress(String.format("Pipeline '%s' is loading...", pipelineName));
            FeedParser parser = FeedParserFactory.getParser(serverUrl, authString);
            long start = System.currentTimeMillis();
            List<Message> messages = new ArrayList<Message>();
            try {
                messages = parser.parse();
            } catch (Throwable t) {
                publishProgress(String.format("Pipeline '%s' load failed", pipelineName));
                Log.wtf("Feed parse failure", t);
            }
            long duration = System.currentTimeMillis() - start;
            Log.d("Go Feeds" , "Parser duration=" + duration);
            publishProgress("Feed Load successful");
            return new Pipeline(messages, pipelineName, serverUrl);
        }

        @Override
        protected Pipeline doInBackground(String... params) {
            return loadSinglePipeline(params[0]);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Toast.makeText(getApplicationContext(), progress[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Pipeline pipeline) {
            pipelineListAdapter.add(pipeline);
            pipelineListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MainMenuOptions.ADD_PIPELINE.ordinal(), MainMenuOptions.ADD_PIPELINE.ordinal(), R.string.add_pipeline);
        menu.add(Menu.NONE, MainMenuOptions.PREFERENCES.ordinal(), MainMenuOptions.PREFERENCES.ordinal(), R.string.preferences);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder addPipelineDialog = new DialogsForPipelineActivity(this).createAddPipelineDialog(new LoadPipelineFeedTask());
        return addPipelineDialog.create();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        MainMenuOptions option = MainMenuOptions.values()[item.getItemId()];
        Log.i("On menu selection" + option.toString(), item.toString());
        if (option == MainMenuOptions.PREFERENCES) {
            Intent myIntent = new Intent();
            myIntent.setClass(getApplicationContext(), PreferencesFromCode.class);
            startActivity(myIntent);
        }
        if (option == MainMenuOptions.ADD_PIPELINE) {
            showDialog(1);
        }
        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent myIntent = new Intent();
        myIntent.setClass(getApplicationContext(), MessageList.class);
        Object itemIdAtPosition = l.getItemAtPosition(position);
        myIntent.putExtra(SELECTED_PIPELINE_URL_KEY, ((Pipeline) itemIdAtPosition).getPipelineFeedUrl());
        startActivity(myIntent);
    }
}
