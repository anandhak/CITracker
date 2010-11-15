package com.thoughtworks.studios.driod.citracker.model;

import java.util.Collections;
import java.util.List;

public class Pipeline implements Comparable<Pipeline>{
	private String title;
    private String pipelineFeedUrl;
    private List<Message> messages;

    public Pipeline(List<Message> messages, String pipelineName, String pipelineFeedUrl) {
        this.messages = messages;
        this.title = pipelineName;
        this.pipelineFeedUrl = pipelineFeedUrl;
    }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(title);
		return sb.toString();
	}


	public int compareTo(Pipeline another) {
		if (another == null) return 1;
		return title.compareTo(another.title);
	}

    public String getPipelineFeedUrl() {
        return pipelineFeedUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getCurrentStatus() {
        return Collections.min(messages).getCategory();
    }
}
