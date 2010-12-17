package com.thoughtworks.studios.driod.citracker;

import com.thoughtworks.studios.driod.citracker.model.Message;

import java.util.List;

public interface FeedParser {
	List<Message> parse();
}
