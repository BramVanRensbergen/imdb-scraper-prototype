package com.bramvanrensbergen.imdb_demo.data;

import java.io.IOException;

import org.jsoup.nodes.Document;

public class Movie extends Title {

	
	private Integer year;
	
	public Movie(String id, Document doc) throws IOException {
		super(id, doc);
		
		findAndSetYearOfRelease();
	}

	
	public Integer getYear() {
		return year;
	}
		
	/**
	 * Lookup the primary actors (the first 15 listed) for the current title in its html Document, and store the value.
	 */
	private void findAndSetYearOfRelease() {
		try {
			this.year = Integer.parseInt(doc.select("#titleYear a").first().text());
		} catch (NumberFormatException e) {
			System.err.println("Could not set year for " + id + " (could not convert element to integer)");
		} catch (NullPointerException e) {
			System.err.println("Could not set year for " + id + " (could not find element)");
		}		
	}


	@Override
	public String getDirectorFunctionName() {
		return "Director";
	}

	@Override
	public String getSubTitle() {
		return null;
	}
}
