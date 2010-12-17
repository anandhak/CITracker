package com.thoughtworks.studios.driod.citracker;

import com.thoughtworks.studios.driod.citracker.model.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

public class DomFeedParser extends BaseFeedParser {

	protected DomFeedParser(String feedUrl) {
		super(feedUrl, "");
	}

	public List<Message> parse() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<Message> messages = new ArrayList<Message>();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.getInputStream());
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName(ENTRY);
			for (int i=0;i<items.getLength();i++){
				Message message = new Message();
				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				for (int j=0;j<properties.getLength();j++){
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase(TITLE)){
						message.setTitle(property.getFirstChild().getNodeValue());
					} else if (name.equalsIgnoreCase(LINK)){
						message.setLink(property.getFirstChild().getNodeValue());
					} else if (name.equalsIgnoreCase(CATEGORY)){
						StringBuilder text = new StringBuilder();
						NodeList chars = property.getChildNodes();
						for (int k=0;k<chars.getLength();k++){
							text.append(chars.item(k).getNodeValue());
						}
						message.setCategory(text.toString());
					} else if (name.equalsIgnoreCase(UPDATE_DATE)){
						message.setUpdatedDate(property.getFirstChild().getNodeValue());
					}
				}
				messages.add(message);
			}
		} catch (Exception e) {
//			throw new RuntimeException(e);
		} 
		return messages;
	}
}
