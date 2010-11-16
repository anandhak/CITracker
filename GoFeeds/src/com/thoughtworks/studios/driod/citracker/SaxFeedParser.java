package com.thoughtworks.studios.driod.citracker;

import android.util.Log;
import com.thoughtworks.studios.driod.citracker.model.Message;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;

public class SaxFeedParser extends BaseFeedParser {

    protected   SaxFeedParser(String feedUrl, String authString){
		super(feedUrl, authString);
	}
	
	public List<Message> parse() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
        RssHandler handler = new RssHandler(10);
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