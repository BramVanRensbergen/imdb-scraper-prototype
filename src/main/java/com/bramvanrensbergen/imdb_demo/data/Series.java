package com.bramvanrensbergen.imdb_demo.data;

import java.io.IOException;

import org.jsoup.nodes.Document;

public class Series extends Title {
	
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
