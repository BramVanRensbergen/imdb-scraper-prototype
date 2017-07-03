package com.bramvanrensbergen.imdb_demo.domain;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Represents a single episode in a TV-Series.
 * @author Bram Van Rensbergen
 */
public class Episode extends Title {
		
	private String seriesName;
	
	private String seriesId;
	
	private String seasonAndEpisodeNumberDesc;
	
	/**
	 * @param id IMDb id of the episode (e.g. 'tt4108304')
	 * @param doc Document containing html of the episode's imdb page.
	 * @throws IOException If {@code doc} is not a valid imdb page.
	 */
	public Episode(String id, Document doc) throws IOException {
		super(id, doc);		
		
		Element parent = doc.select(".titleParent a").first();		
		
		seriesName = parent.text();
		seriesId = Title.getIdFromUrl(parent.attr("href"));
		
		Element seasonInfo = doc.select(".navigation_panel .bp_heading").first();		
		seasonAndEpisodeNumberDesc = seasonInfo.text();		
		
		this.yearOfRelease = obtainYearOfReleaseFromHtml();
	}
	
	/**
	 * @return Name of the series of which this episode is part.
	 */
	public String getSeriesName() {
		return seriesName;
	}

	/**
	 * @return IMDb id of the series of which this episode is part.
	 */
	public String getSeriesId() {
		return seriesId;
	}
	
	/**
	 * @return String containing season and episode number of this episode.
	 */
	public String getSeasonAndEpisodeNumberDesc() {
		return seasonAndEpisodeNumberDesc;
	}

	@Override
	public String getDirectorFunctionName() {
		return "Director";
	}

	@Override
	public String getSubTitle() {
		return seriesName + ", " + seasonAndEpisodeNumberDesc.replace(" |", ", ");
	}	
}