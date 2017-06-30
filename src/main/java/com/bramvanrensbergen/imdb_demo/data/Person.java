package com.bramvanrensbergen.imdb_demo.data;

public class Person {
	
	private static final String BASE_URL = "http://www.imdb.com/name/";
			
	private final String id;
	
	private String name;
	
	private String url;
	
	/**
	 * // full link is in format: /name/nm0000186?ref_=tt_ov_dr
	 * @param url
	 * @return
	 */
	public static String getIdFromUrl(String url) {
		return url.split("/name/")[1].split("\\?")[0];
	}
	
//	public Person(String id) {
//		if (id == null || id.isEmpty()) {
//			throw new IllegalArgumentException("No valid id provided, please provide in the format 'nm0000186'.");
//		}
//		
//		this.id = id;
//		
//		setNameFromId();
//		
//		this.url = BASE_URL + id;
//	}
		
	public Person(String id, String name) {
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("No valid id provided, please provide in the format 'nm0000186'.");
		}
		
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("No valid name provided");
		}
		
		this.id = id;
		this.name = name;		
		this.url = BASE_URL + id;
	}
	
//	private void setNameFromId() {
//		//TODO
//	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}
	

}
