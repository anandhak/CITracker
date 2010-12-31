package com.thoughtworks.studios.driod.citracker.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
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
        reload();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        pipelines = FeedParserFactory.loadPipelines(this);
        this.setListAdapter(new PipelineListAdapter(this, R.layout.row, pipelines));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        reload();
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
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.add_pipeline_dialog, null);
        return new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setTitle(R.string.add_pipeline)
                .setView(textEntryView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText editView = (EditText) textEntryView.findViewById(R.id.pipelinename_edit);
                        String newPipelineName = editView.getText().toString().trim();
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String existingPipelines = preferences.getString("pipeline_names", "");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("pipeline_names", existingPipelines + "," + newPipelineName);
                        editor.commit();
                        reload();
                        Toast.makeText(PipelineList.this.getApplicationContext(), String.format("Pipeline %s added.", newPipelineName), Toast.LENGTH_LONG).show();
                    }
                }).create();
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
        myIntent.putExtra(SELECTED_PIPELINE_URL_KEY, pipelines.get(position).getPipelineFeedUrl());
        startActivity(myIntent);
    }

}
