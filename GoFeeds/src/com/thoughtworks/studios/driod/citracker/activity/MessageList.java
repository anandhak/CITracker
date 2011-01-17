package com.thoughtworks.studios.driod.citracker.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.thoughtworks.studios.driod.citracker.view.PipelineStatusListAdapter;

import java.util.List;

public class MessageList extends ListActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(com.thoughtworks.studios.driod.citracker.R.layout.main);
        reloadEntries();
    }

    private class LoadPipelineStatusFeedTask extends AsyncTask<String, String, List<Message>> {

        public List<Message> loadFeed(String feedUrl) {
            Context applicationContext = MessageList.this.getApplicationContext();
            String authString = FeedParserFactory.getAuthString(applicationContext);
            FeedParser parser = FeedParserFactory.getParser(feedUrl, authString);
            long start = System.currentTimeMillis();
            List<Message> messages = parser.parse();
            long duration = System.currentTimeMillis() - start;
            Log.i("Go Feeds", "Parser duration=" + duration);
            return messages;
        }

        @Override
        protected List<Message> doInBackground(String... params) {
            return loadFeed(params[0]);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Toast.makeText(getApplicationContext(), progress[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(List<Message> messages) {
            ArrayAdapter<Message> adapter = new PipelineStatusListAdapter(MessageList.this, R.layout.row, messages);
            MessageList.this.setListAdapter(adapter);
        }
    }


    private void reloadEntries() {
        String currentPipeline = getIntent().getStringExtra(PipelineList.SELECTED_PIPELINE_URL_KEY);
        new LoadPipelineStatusFeedTask().execute(currentPipeline);
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
        Object itemIdAtPosition = l.getItemAtPosition(position);
        String externalUrl = ((Message) itemIdAtPosition).getLink().toExternalForm();
        Log.e("On Browser", String.format("On stage selection navigate to %s on browser!", externalUrl));
        Intent viewMessage = new Intent(Intent.ACTION_VIEW, Uri.parse(externalUrl));
        this.startActivity(viewMessage);
    }

}