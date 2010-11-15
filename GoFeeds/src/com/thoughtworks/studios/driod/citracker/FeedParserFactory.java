package com.thoughtworks.studios.driod.citracker;

public abstract class FeedParserFactory {

    public static FeedParser getParser(String feedUrl) {
        return new SaxFeedParser(feedUrl);
    }
}
