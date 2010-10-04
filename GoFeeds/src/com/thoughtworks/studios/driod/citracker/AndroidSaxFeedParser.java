package com.thoughtworks.studios.driod.citracker;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import com.thoughtworks.studios.driod.citracker.model.Message;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AndroidSaxFeedParser extends BaseFeedParser {

    static final String FEED = "feed";

    public AndroidSaxFeedParser(String feedUrl) {
        super(feedUrl);
    }

    public List<Message> parse() {
        final Message currentMessage = new Message();
        RootElement root = new RootElement("http://www.w3.org/2005/Atom", FEED);
        final List<Message> messages = new ArrayList<Message>();
        Element pipelineName = root.getChild(PIPELINE_NAME);
        Element entry = root.getChild(ENTRY);
        entry.setEndElementListener(new EndElementListener() {
            public void end() {
                messages.add(currentMessage.copy());
                if(messages.size() >= 10){
                    throw new RuntimeException("Need only 10 entries!");
                }
            }
        });
        entry.getChild(TITLE).setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                currentMessage.setTitle(body);
            }
        });
        entry.getChild(LINK).setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                currentMessage.setLink(body);
            }
        });
        entry.getChild(CATEGORY).setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                currentMessage.setCategory(body);
            }
        });
        entry.getChild(UPDATE_DATE).setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                currentMessage.setUpdatedDate(body);
            }
        });
        try {
//            feedString = slurp(this.getInputStream());
//            Log.i(feedString, "AndriodSaxFeedParser");
//			Xml.parse(feedString, root.getContentHandler());
            Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
//            throw new RuntimeException(e);
            Log.i("AndriodSaxFeedParser", String.format("Parsing ended with %d entries , or with %s", messages.size(), e.getMessage()));            
        }
        return messages;
    }

    public static String slurp(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        int numberOfBytes = 2048 / 10;
        byte[] b = new byte[numberOfBytes];
//        for (int n; (n = in.read(b)) != -1;) {
//            out.append(new String(b, 0, n));
//        }
        int i = in.read(b, 0, numberOfBytes);
        out.append(new String(b, 0, numberOfBytes));
        return out.toString();
    }

}
