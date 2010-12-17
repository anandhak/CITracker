package com.thoughtworks.studios.driod.citracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.thoughtworks.studios.driod.citracker.model.Message;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;

import java.util.ArrayList;
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

    public static List<Pipeline> loadPipelines(Context context) {
        String authString = getAuthString(context);
        String serverString = getServerString(context);
        List<String> pipelineNames = getPipelineString(context);
        List<Pipeline> pipelines = new ArrayList<Pipeline>();
        for (String name : pipelineNames) {
            try {
                String serverUrl = String.format(serverString + "/go/api/pipelines/%s/stages.xml", name);
                pipelines.add(loadSinglePipeline(name, serverUrl, authString));
            } catch (Throwable t) {
                Log.e("LOAD FAILED", String.format("Unable to load pipeline %s with auth :%s for url %s", name, authString, serverString), t);
            }
        }
        return pipelines;
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
        Log.i("Using auth", String.format("<<<%s>>>:****", preferences.getString("username", "")));
        return authString;
    }

    public static String getServerString(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("serverUrl", "http://go02.thoughtworks.com:8153");
    }

    public static List<String> getPipelineString(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String pipelineString = preferences.getString("pipeline_names", "cleanArtifacts");
        return Arrays.asList(pipelineString.split(","));
    }

}
