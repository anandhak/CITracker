package com.thoughtworks.studios.driod.citracker;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class BaseFeedParser implements FeedParser {

    // names of the XML tags
    static final String PIPELINE_NAME = "title";
    static final String UPDATE_DATE = "updated";
    static final String CATEGORY = "category";
    static final String LINK = "link";
    static final String TITLE = "title";
    static final String ENTRY = "entry";

    private final String feedUrl;
    private String authString;

    protected BaseFeedParser(String feedUrl, String authString) {
        this.feedUrl = feedUrl;
        this.authString = authString;
    }

    protected InputStream getInputStream() {
        String authMessage = "";
        try {
            URLConnection urlConnection = new URL(feedUrl).openConnection();
            authMessage = String.format("on url %s using %s", feedUrl, authString);
            Log.i("Auth", authMessage);
            String encoding = String.valueOf(Base64Coder.encode(authString.getBytes()));
            urlConnection.setRequestProperty("Authorization", "Basic " + encoding);
            return urlConnection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Failed to auth " + authMessage, e);
        }
    }
}
