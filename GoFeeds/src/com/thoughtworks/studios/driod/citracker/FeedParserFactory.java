package com.thoughtworks.studios.driod.citracker;

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

	public static List<Pipeline> loadPipelines(List<Pipeline> pipelines, String userAuth) {
		try {
			pipelines.add(loadSinglePipeline(PipelineList.PIPELINE_1,
					String.format(PipelineList.feedUrl, PipelineList.PIPELINE_1), userAuth));
			pipelines.add(loadSinglePipeline(PipelineList.PIPELINE_2,
					String.format(PipelineList.feedUrl, PipelineList.PIPELINE_2), userAuth));
		} catch (Throwable t) {
			Log.e("Go Feeds", t.getMessage(), t);
		}
		return pipelines;
	}

	public static String getAuthString(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String authString = preferences.getString("username", "") + ":"
				+ preferences.getString("password", "");
		Log.i("Using prefs", String.format("on url %s using <<<%s>>>", PipelineList.feedUrl,
				authString));
		return authString;
	}
}
