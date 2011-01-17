package com.thoughtworks.studios.driod.citracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

public abstract class FeedParserFactory {

    public static FeedParser getParser(String feedUrl, String authString) {
        return new SaxFeedParser(feedUrl, authString);
    }

    public static String getAuthString(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String authString = preferences.getString("username", "akrishna") + ":" + preferences.getString("password", "N@nkr15h83");
        return authString;
    }

    public static String getServerString(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("serverUrl", "https://cruise01.thoughtworks.com");
    }

    public static List<String> getPipelineString(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String pipelineString = preferences.getString("pipeline_names", "tlb");
        return Arrays.asList(pipelineString.split(","));
    }

}
