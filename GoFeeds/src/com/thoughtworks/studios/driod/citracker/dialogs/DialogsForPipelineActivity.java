package com.thoughtworks.studios.driod.citracker.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.thoughtworks.studios.driod.citracker.R;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;

/**
 * UI element which provides options to add new dialog
 */
public class DialogsForPipelineActivity {
    private final Context parent;

    public DialogsForPipelineActivity(Context parent) {
        this.parent = parent;
    }

    public AlertDialog.Builder createAddPipelineDialog(final AsyncTask<String, String, Pipeline> task) {
        LayoutInflater factory = LayoutInflater.from(parent);
        final View textEntryView = factory.inflate(R.layout.add_pipeline_dialog, null);
        return new AlertDialog.Builder(parent)
                .setIcon(R.drawable.icon)
                .setTitle(R.string.add_pipeline)
                .setView(textEntryView)
                .setPositiveButton(R.string.ok, new AddPipelineAction(textEntryView, task));
    }

    private void updatePipelinesPreferences(String newPipelineName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent.getApplicationContext());
        String existingPipelines = preferences.getString("pipeline_names", "");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pipeline_names", existingPipelines + "," + newPipelineName);
        editor.commit();
    }

    class AddPipelineAction implements DialogInterface.OnClickListener {
        private View textEntryView;
        private AsyncTask<String, String, Pipeline> addActionTask;

        public AddPipelineAction(View textEntryView, AsyncTask<String, String, Pipeline> addActionTask) {
            this.textEntryView = textEntryView;
            this.addActionTask = addActionTask;
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            EditText editView = (EditText) textEntryView.findViewById(R.id.pipelinename_edit);
            String userInput = editView.getText().toString();
            String newPipelineName = userInput.trim();
            updatePipelinesPreferences(newPipelineName);
            Toast.makeText(parent.getApplicationContext(),
                    String.format("Pipeline %s added.", newPipelineName), Toast.LENGTH_LONG).show();
            addActionTask.execute(newPipelineName);
        }
    }

}
