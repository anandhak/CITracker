package com.thoughtworks.studios.driod.citracker;

import android.util.Log;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SaxFeedParser extends BaseFeedParser {

    protected   SaxFeedParser(String feedUrl){
		super(feedUrl);
	}
	
	public List<Message> parse() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
        RssHandler handler = new RssHandler();
        try {
            SAXParser parser = factory.newSAXParser();
			parser.parse(this.getInputStream(), handler);
			return handler.getMessages();
		} catch (Exception e) {
            List<Message> messages = handler.getMessages();
            Log.i("SaxFeedParser", String.format("Parsing ended with %d entries , or with %s", messages.size(), e.getMessage()));
            return messages;
		} 
	}
}