package com.thoughtworks.studios.driod.citracker.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.thoughtworks.studios.driod.citracker.FeedParser;
import com.thoughtworks.studios.driod.citracker.FeedParserFactory;
import com.thoughtworks.studios.driod.citracker.MainMenuOptions;
import com.thoughtworks.studios.driod.citracker.R;
import com.thoughtworks.studios.driod.citracker.model.Message;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;
import com.thoughtworks.studios.driod.citracker.service.LocalService;
import com.thoughtworks.studios.driod.citracker.view.PipelineListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Understands named pipeline and show them with the information from the
 * lastest run. Pass/fail
 */
public class PipelineList extends ListActivity {
    public static final String SELECTED_PIPELINE_URL_KEY = "com.thoughtworks.studios.driod.citracker.activity.CurrentPipelineName";
    private boolean mIsBound;
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
            String serverUrl = String.format(serverString + "/go/api/pipelines/%s/stages.xml", pipelineName);
            String authString = FeedParserFactory.getAuthString(applicationContext);
            publishProgress("Pipeline List" + "loading feeds" + serverUrl);
            FeedParser parser = FeedParserFactory.getParser(serverUrl, authString);
            long start = System.currentTimeMillis();
            List<Message> messages = new ArrayList<Message>();
            try {
                    messages = parser.parse();
            } catch (Throwable t) {
                publishProgress(
                        String.format("Unable to load pipeline %s with auth :%s for url %s",
                                pipelineName,
                                authString,
                                serverUrl));
            }
            long duration = System.currentTimeMillis() - start;
            publishProgress("Go Feeds" + "Parser duration=" + duration);
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
        menu.add(Menu.NONE, MainMenuOptions.ADD_GO_URL.ordinal(), MainMenuOptions.ADD_GO_URL.ordinal(), R.string.add_go_url);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.add_pipeline_dialog, null);
        return new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setTitle(R.string.add_pipeline)
                .setView(textEntryView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText editView = (EditText) textEntryView.findViewById(R.id.pipelinename_edit);
                        String userInput = editView.getText().toString();
                        String newPipelineName = userInput.trim();
                        updatePipelinesPreferences(newPipelineName);
                        Toast.makeText(PipelineList.this.getApplicationContext(), String.format("Pipeline %s added.", newPipelineName), Toast.LENGTH_LONG).show();
                        new LoadPipelineFeedTask().execute(newPipelineName);
                    }
                }).create();
    }

    private void updatePipelinesPreferences(String newPipelineName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String existingPipelines = preferences.getString("pipeline_names", "");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pipeline_names", existingPipelines + "," + newPipelineName);
        editor.commit();
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
        if (option == MainMenuOptions.ADD_GO_URL) {
            createService();
        }
        return false;
    }

    private LocalService mBoundService;

    private void createService() {
        doBindService();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((LocalService.LocalBinder) service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(PipelineList.this, R.string.choose_pipeline,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(PipelineList.this, R.string.add_go_url,
                    Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent myIntent = new Intent();
        myIntent.setClass(getApplicationContext(), MessageList.class);
        Object itemIdAtPosition = l.getItemAtPosition(position);
        myIntent.putExtra(SELECTED_PIPELINE_URL_KEY, ((Pipeline) itemIdAtPosition).getPipelineFeedUrl());
        startActivity(myIntent);
    }


    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(PipelineList.this,
                LocalService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }


}
