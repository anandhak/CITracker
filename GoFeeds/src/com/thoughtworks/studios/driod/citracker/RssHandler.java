package com.thoughtworks.studios.driod.citracker;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static com.thoughtworks.studios.driod.citracker.BaseFeedParser.*;

public class RssHandler extends DefaultHandler {
    private List<Message> messages = new ArrayList<Message>();
    private Message currentMessage;
    private StringBuilder builder;

    public List<Message> getMessages() {
        return this.messages;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        super.endElement(uri    , localName, name);
        if (this.currentMessage != null) {
            if (localName.equalsIgnoreCase(TITLE)) {
                currentMessage.setTitle(builder.toString());
            } else if (localName.equalsIgnoreCase(UPDATE_DATE)) {
                currentMessage.setUpdatedDate(builder.toString());
            } else if (localName.equalsIgnoreCase(ENTRY)) {
                if (currentMessage.getCategory() != null) {       //TODO Hack to fix reading header bug
                    messages.add(currentMessage);
                }
                limitFeedEntries();
            }
        }
        builder.setLength(0);
    }

    private void limitFeedEntries() {
        if (messages.size() >= 10) {
            throw new RuntimeException("Need only 10 entries!");
        }
    }


    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        messages = new ArrayList<Message>();
        builder = new StringBuilder();
        currentMessage = null;
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        if (localName.equalsIgnoreCase(ENTRY)) {
            this.currentMessage = new Message();
        } else if (localName.equalsIgnoreCase(LINK) && attributes.getValue("rel").equalsIgnoreCase("alternate")) {
            currentMessage.setLink(attributes.getValue("href"));
        } else if (localName.equalsIgnoreCase(CATEGORY) && isStatusCategory(attributes)) {
            currentMessage.setCategory(attributes.getValue("term"));
        }
    }

    private boolean isStatusCategory(Attributes attributes) {
        return attributes.getValue("term").equalsIgnoreCase("cancelled")
                ||attributes.getValue("term").equalsIgnoreCase("passed")
                ||attributes.getValue("term").equalsIgnoreCase("failed");
    }
}