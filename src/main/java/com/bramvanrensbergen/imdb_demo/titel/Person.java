package com.bramvanrensbergen.imdb_demo.data;

/**
 * Object representing an actor or director/creator.
 * @author Bram Van Rensbergen 
 */
public class Person {
	
	private static final String BASE_URL = "http://www.imdb.com/name/";
			
	private final String id;
	
	private String name;
	
	private String url;
	
	/**
	 * Get the imdb id contained in the indicated url.
	 * @param url containing a title's id, e.g. /name/nm0000186?ref_=tt_ov_dr
	 * @return the id, e.g. nm0000186
	 */
	public static String getIdFromUrl(String url) {
		return url.split("/name/")[1].split("\\?")[0];
	}
	
	/**
	 * Create an object representign an actor/director/creator with the indicated name and IMDb id.
	 * @param id IMDb id, e.g. 'nm0000186'
	 * @param name
	 */
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

	/**
	 * @return IMDb id of the person, e.g. 'nm0000186'
	 */
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * @return URL to the person's IMDb page.
	 */
	public String getUrl() {
		return url;
	}
	

}
