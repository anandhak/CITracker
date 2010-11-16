package com.thoughtworks.studios.driod.citracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thoughtworks.studios.driod.citracker.activity.PipelineList;
import com.thoughtworks.studios.driod.citracker.model.Message;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;

public abstract class FeedParserFactory {

	public static FeedParser getParser(String feedUrl, String authString) {
		return new SaxFeedParser(feedUrl, authString);
	}

	public static Pipeline loadSinglePipeline(String pipelineName, String feedUrl, String authString) {
		Log.i("Pipeline List", "loading feeds" + feedUrl);
		FeedParser parser = getParser(feedUrl, authString);
		long start = System.currentTimeMillis();
		List<Message> messages = parser.parse();
		long duration = System.currentTimeMillis() - start;
		Log.i("Go Feeds", "Parser duration=" + duration);
		return new Pipeline(messages, pipelineName, feedUrl);
	}

	public static List<Pipeline> loadPipelines(Context context) {
		List<String> pipelineNames = getPipelineString(context);
		List<Pipeline> pipelines = new ArrayList<Pipeline>();
		try {
			for (String name : pipelineNames) {
				pipelines.add(loadSinglePipeline(name, String.format(PipelineList.feedUrl, name), getAuthString(context)));
			}
		} catch (Throwable t) {
			Log.e("Go Feeds", t.getMessage(), t);
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
		Log.i("Using prefs", String.format("on url %s using <<<%s>>>", PipelineList.feedUrl, authString));
		return authString;
	}

	public static String getServerString(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString("serverUrl", "go01.thoughtworks.com:8153");
	}

	public static List<String> getPipelineString(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String pipelineString = preferences.getString("pipeline_names", "cruise,acceptance");
		return Arrays.asList(pipelineString.split(","));
	}

}
