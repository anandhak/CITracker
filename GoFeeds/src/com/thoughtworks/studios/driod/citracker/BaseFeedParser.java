package com.thoughtworks.studios.driod.citracker;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public abstract class BaseFeedParser implements FeedParser {

	// names of the XML tags
	static final String PIPELINE_NAME = "title";
	static final String UPDATE_DATE = "updated";
	static final  String CATEGORY = "category";
	static final  String LINK = "link";
	static final  String TITLE = "title";
	static final  String ENTRY = "entry";
	
	private final URL feedUrl;
	//TODO: THIS SHOULD GO AWAY
    private String theUsername = "";
    private String thePassword = "";

    protected BaseFeedParser(String feedUrl){
		try {
			this.feedUrl = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	protected InputStream getInputStream() {
		try {
            URLConnection urlConnection = feedUrl.openConnection();
            String userPassword = theUsername + ":" + thePassword;
            String encoding = String.valueOf(Base64Coder.encode(userPassword.getBytes()));
            urlConnection.setRequestProperty ("Authorization", "Basic " + encoding);
            return urlConnection.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}