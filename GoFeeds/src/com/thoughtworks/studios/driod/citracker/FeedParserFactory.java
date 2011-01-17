package com.thoughtworks.studios.driod.citracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.thoughtworks.studios.driod.citracker.model.Message;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;

import java.util.Arrays;
import java.util.List;

public abstract class FeedParserFactory {

    public static FeedParser getParser(String feedUrl, String authString) {
        return new SaxFeedParser(feedUrl, authString);
    }

    private static Pipeline loadSinglePipeline(String pipelineName, String feedUrl, String authString) {
        Log.i("Pipeline List", "loading feeds" + feedUrl);
        FeedParser parser = getParser(feedUrl, authString);
        long start = System.currentTimeMillis();
        List<Message> messages = parser.parse();
        long duration = System.currentTimeMillis() - start;
        Log.i("Go Feeds", "Parser duration=" + duration);
        return new Pipeline(messages, pipelineName, feedUrl);
    }

    public static List<Message> loadFeed(String feedUrl, String userAuth) {
        FeedParser parser = getParser(feedUrl, userAuth);
        long start = System.currentTimeMillis();
        List<Message> messages = parser.parse();
        long duration = System.currentTimeMillis() - start;
        Log.i("Go Feeds", "Parser duration=" + duration);
        return messages;
    }

    public static String getAuthString(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String authString = preferences.getString("username", "") + ":" + preferences.getString("password", "");
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
