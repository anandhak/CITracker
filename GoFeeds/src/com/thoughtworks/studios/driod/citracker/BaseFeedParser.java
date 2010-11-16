package com.thoughtworks.studios.driod.citracker;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public abstract class BaseFeedParser implements FeedParser {

	// names of the XML tags
	static final String PIPELINE_NAME = "title";
	static final String UPDATE_DATE = "updated";
	static final String CATEGORY = "category";
	static final String LINK = "link";
	static final String TITLE = "title";
	static final String ENTRY = "entry";

	private final URL feedUrl;
	private String authString;

	protected BaseFeedParser(String feedUrl, String authString) {
		try {
			this.feedUrl = new URL(feedUrl);
			this.authString = authString;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	protected InputStream getInputStream() {
		try {
			URLConnection urlConnection = feedUrl.openConnection();
			Log.i("Auth", String.format("on url %s using %s", feedUrl,
					authString));
			String encoding = String.valueOf(Base64Coder.encode(authString
					.getBytes()));
			urlConnection.setRequestProperty("Authorization", "Basic "
					+ encoding);
			return urlConnection.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
