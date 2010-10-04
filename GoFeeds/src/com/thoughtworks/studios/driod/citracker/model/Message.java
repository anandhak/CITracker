package com.thoughtworks.studios.driod.citracker.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Comparable<Message>{
	static SimpleDateFormat FORMATTER = 
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
	private String title;
	private URL link;
	private String category;
	private Date updatedDate;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title.trim();
	}
	// getters and setters omitted for brevity 
	public URL getLink() {
		return link;
	}
	
	public void setLink(String link) {
		try {
			this.link = new URL(link);
		} catch (MalformedURLException e) {
//			throw new RuntimeException(e);
		}
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category.trim();
	}

	public String getUpdatedDate() {
		return FORMATTER.format(this.updatedDate);
	}

	public void setUpdatedDate(String date) {
		// pad the updatedDate if necessary
		while (!date.endsWith("00")){
			date += "0";
		}
		try {
			this.updatedDate = FORMATTER.parse(date.trim());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Message copy(){
		Message copy = new Message();
		copy.title = title;
		copy.link = link;
		copy.category = category;
		copy.updatedDate = updatedDate;
		return copy;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(title);
		sb.append('\n');
		sb.append(this.getUpdatedDate());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((updatedDate == null) ? 0 : updatedDate.hashCode());
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (updatedDate == null) {
			if (other.updatedDate != null)
				return false;
		} else if (!updatedDate.equals(other.updatedDate))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	public int compareTo(Message another) {
		if (another == null) return 1;
		// sort descending, most recent first
		return another.updatedDate.compareTo(updatedDate);
	}
}
