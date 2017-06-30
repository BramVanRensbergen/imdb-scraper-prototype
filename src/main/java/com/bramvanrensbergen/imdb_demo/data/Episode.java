package com.bramvanrensbergen.imdb_demo.data;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Episode extends Title {
	
	private String seriesName;
	
	private String seriesId;
	
	private String seasonAndEpisodeNumberDesc;
	
	public Episode(String id, Document doc) throws IOException {
		super(id, doc);		
		
		Element parent = doc.select(".titleParent a").first();		
		
		seriesName = parent.text();
		seriesId = Title.getIdFromUrl(parent.attr("href"));
		
		Element seasonInfo = doc.select(".navigation_panel .bp_heading").first();		
		seasonAndEpisodeNumberDesc = seasonInfo.text();
		
	}
	
	public String getSeriesName() {
		return seriesName;
	}

	public String getSeriesId() {
		return seriesId;
	}
	
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
