package com.bramvanrensbergen.imdb_demo.titel;

import java.io.IOException;

import org.jsoup.nodes.Document;

public class Movie extends Title {
		
	/**
	 * @param id IMDb id of the movie (e.g. 'tt0090756')
	 * @param doc Document containing html of the movie's imdb page.
	 * @throws IOException If {@code doc} is not a valid imdb page.
	 */
	public Movie(String id, Document doc) throws IOException {
		super(id, doc);
		
		this.yearOfRelease = obtainYearOfReleaseFromHtml();
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
