package com.bramvanrensbergen.imdb_demo.data;

import java.io.IOException;

import org.jsoup.nodes.Document;

public class Movie extends Title {
	
	private Integer year;
	
	/**
	 * @param id IMDb id of the movie (e.g. 'tt0090756')
	 * @param doc Document containing html of the movie's imdb page.
	 * @throws IOException If {@code doc} is not a valid imdb page.
	 */
	public Movie(String id, Document doc) throws IOException {
		super(id, doc);
		
		findAndSetYearOfRelease();
	}

	/**
	 * @return Year in which movie was released.
	 */
	public Integer getYear() {
		return year;
	}
		
	/**
	 * Lookup the year of the movie's release its html Document, and store the value.
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
