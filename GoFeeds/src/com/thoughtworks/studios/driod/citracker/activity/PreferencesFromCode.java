package com.thoughtworks.studios.driod.citracker.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.widget.Toast;

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
		Toast.makeText(getApplicationContext(), "Saving configuration...", Toast.LENGTH_SHORT).show();
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
        
//TODO Enable Auth on UI based on checkbox to be implemented below
        // Toggle preference
//        CheckBoxPreference togglePref = new CheckBoxPreference(this);
//        togglePref.setKey("toggle_preference");
//        togglePref.setTitle(R.string.title_toggle_preference);
//        togglePref.setSummary(R.string.summary_toggle_preference);
//        inlinePrefCat.addPreference(togglePref);

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

        // Launch preferences
//        PreferenceCategory launchPrefCat = new PreferenceCategory(this);
//        launchPrefCat.setTitle(R.string.launch_preferences);
//        root.addPreference(launchPrefCat);

        /*
         * The Preferences screenPref serves as a screen break (similar to page
         * break in word processing). Like for other preference types, we assign
         * a key here so that it is able to save and restore its instance state.
         */
        // Screen preference
//        PreferenceScreen screenPref = getPreferenceManager().createPreferenceScreen(this);
//        screenPref.setKey("screen_preference");
//        screenPref.setTitle(R.string.title_screen_preference);
//        screenPref.setSummary(R.string.summary_screen_preference);
//        launchPrefCat.addPreference(screenPref);

        /*
         * You can add more preferences to screenPref that will be shown on the
         * next screen.
         */

//        // Example of next screen toggle preference
//        CheckBoxPreference nextScreenCheckBoxPref = new CheckBoxPreference(this);
//        nextScreenCheckBoxPref.setKey("next_screen_toggle_preference");
//        nextScreenCheckBoxPref.setTitle(R.string.title_next_screen_toggle_preference);
//        nextScreenCheckBoxPref.setSummary(R.string.summary_next_screen_toggle_preference);
//        screenPref.addPreference(nextScreenCheckBoxPref);
//
//        // Intent preference
//        PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(this);
//        intentPref.setIntent(new Intent().setAction(Intent.ACTION_VIEW)
//                .setData(Uri.parse("http://www.android.com")));
//        intentPref.setTitle(R.string.title_intent_preference);
//        intentPref.setSummary(R.string.summary_intent_preference);
//        launchPrefCat.addPreference(intentPref);
//
//        // Preference attributes
//        PreferenceCategory prefAttrsCat = new PreferenceCategory(this);
//        prefAttrsCat.setTitle(R.string.preference_attributes);
//        root.addPreference(prefAttrsCat);
//
//        // Visual parent toggle preference
//        CheckBoxPreference parentCheckBoxPref = new CheckBoxPreference(this);
//        parentCheckBoxPref.setTitle(R.string.title_parent_preference);
//        parentCheckBoxPref.setSummary(R.string.summary_parent_preference);
//        prefAttrsCat.addPreference(parentCheckBoxPref);
//
//        // Visual child toggle preference
//        // See res/values/attrs.xml for the <declare-styleable> that defines
//        // TogglePrefAttrs.
//        TypedArray a = obtainStyledAttributes(R.styleable.TogglePrefAttrs);
//        CheckBoxPreference childCheckBoxPref = new CheckBoxPreference(this);
//        childCheckBoxPref.setTitle(R.string.title_child_preference);
//        childCheckBoxPref.setSummary(R.string.summary_child_preference);
//        childCheckBoxPref.setLayoutResource(
//                a.getResourceId(R.styleable.TogglePrefAttrs_android_preferenceLayoutChild,
//                        0));
//        prefAttrsCat.addPreference(childCheckBoxPref);
//        a.recycle();

        return root;
    }
}