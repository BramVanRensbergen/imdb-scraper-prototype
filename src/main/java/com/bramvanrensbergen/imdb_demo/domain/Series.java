package com.bramvanrensbergen.imdb_demo.domain;

import java.io.IOException;

import org.jsoup.nodes.Document;

/**
 * Object representing an entire tv-series.
 * @author Bram Van Rensbergen 
 */
public class Series extends Title {
	
	/**
	 * @param id IMDb id of the TV-series (e.g. 'tt4093826')
	 * @param doc Document containing html of the series' imdb page.
	 * @throws IOException If {@code doc} is not a valid imdb page.
	 */
	public Series(String id, Document doc) throws IOException {
		super(id, doc);				
	}
	
	@Override
	public String getDirectorFunctionName() {
		return "Creator";
	}

	@Override
	public String getSubTitle() {
		return null;
	} 
}
