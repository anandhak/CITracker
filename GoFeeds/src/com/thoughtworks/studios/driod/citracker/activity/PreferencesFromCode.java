package com.thoughtworks.studios.driod.citracker.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import com.thoughtworks.studios.driod.citracker.R;

public class PreferencesFromCode extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(createPreferenceHierarchy());
    }

    
    @Override
	protected void onStop() {
		super.onStop();
//		Toast.makeText(getApplicationContext(), "Saving configuration...", Toast.LENGTH_SHORT).show();
	}


	private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        PreferenceCategory inlinePrefCat = new PreferenceCategory(this);
        inlinePrefCat.setTitle(R.string.server_base_url);
        inlinePrefCat.setSummary(R.string.go_url_example);
        root.addPreference(inlinePrefCat);

        EditTextPreference serverUrl = new EditTextPreference(this);
        serverUrl.setDialogTitle(R.string.server_base_url);
		serverUrl.setKey("serverUrl");
        serverUrl.setTitle(R.string.server_base_url);
        inlinePrefCat.addPreference(serverUrl);

        PreferenceCategory dialogBasedPrefCat = new PreferenceCategory(this);
        dialogBasedPrefCat.setTitle(R.string.credentials);
        dialogBasedPrefCat.setSummary(R.string.summary_edittext_preference);
        root.addPreference(dialogBasedPrefCat);

        // Edit text preference
        EditTextPreference userName = new EditTextPreference(this);
        userName.setDialogTitle(R.string.username);
        userName.setKey("username");
        userName.setTitle(R.string.username);
        userName.getEditText().setTransformationMethod(SingleLineTransformationMethod.getInstance());
        dialogBasedPrefCat.addPreference(userName);
        
        // Edit text preference
        EditTextPreference password = new EditTextPreference(this);
        password.setDialogTitle(R.string.password);
        password.setKey("password");
        password.setTitle(R.string.password);
        password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        dialogBasedPrefCat.addPreference(password);

        // Add pipeline
        EditTextPreference pipelineName = new EditTextPreference(this);
        pipelineName.setDialogTitle(R.string.pipelines);
        pipelineName.setKey("pipeline_names");
        pipelineName.setTitle(R.string.pipeline);
        pipelineName.setSummary(R.string.pipelines_example);
        dialogBasedPrefCat.addPreference(pipelineName);
       return root;
    }
}