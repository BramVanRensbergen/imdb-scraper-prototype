package com.bramvanrensbergen.imdb_demo.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Object representing a movie, series, or episode of a series.
 * @author Bram Van Rensbergen 
 */
public abstract class Title {
	private static final String BASE_URL = "http://www.imdb.com/title/";
	
	/**
	 * When a list of titles is passed to the web service, they can be separated by a number of characters; this regex string splits them apart.
	 */
	private static final String TITLE_SEPARATOR_REGEX = "%20| |\\+|,";
		
	protected final String id;
	
	protected Document doc;	
	
	private String url;
	
	private String title;
	
	private String genres;
	
	private Set<String> genresSet;
		
	private Double rating;	
	
	private String summaryText;

	/**
	 * The director(s) (for episodes and movies) or creator(s) (for series) of the current title.
	 */
	private ArrayList<Person> directorsOrCreators;

	// currently 15
	private ArrayList<Person> primaryActors;

	/**
	 * Create a list with Title objects for each title-id in the indicated strings; titles can be separated by space, comma, plus, or '%20'.
	 * @param title_ids A string of title-ids (format 'tt0090756')
	 * @return List of Title objects corresponding to those ids.
	 * @throws IOException If the title-id is not valid i.e. the corresponding imdb page cannot be found.
	 */
	public static List<Title> createTitles(String title_ids) throws IOException {
		ArrayList<Title> titlesList = new ArrayList<Title>();
		
		String[] titleIds = title_ids.split(TITLE_SEPARATOR_REGEX);
		
		for (String id : titleIds) {
			Document doc = Jsoup.connect(BASE_URL + id).get();
			Element e = doc.select(".titleBar .subtext a:last-child").first();
			
			if (e == null) {
				System.err.println("Could not obtain type of title for " + id + ", skipping");
				continue;
			}
			
			String desc = e.text();
			
			Title t;
			
			if (desc.contains("TV Series")) {
				t = new Series(id, doc);
			} else if (desc.contains("Episode air")) {
				t = new Episode(id, doc);
			} else {
				t = new Movie(id, doc);
			}			
			
			titlesList.add(t);
		}
		
		return titlesList;
	}
	
	/**
	 * Get the imdb id contained in the indicated url.
	 * @param url containing a title's id, e.g. /name/tt0000186?ref_=tt_ov_dr
	 * @return the id, e.g. tt0000186
	 */
	public static String getIdFromUrl(String url) {
		return url.split("/title/")[1].split("\\?")[0];
	}
		
	protected Title(String id, Document doc) throws IOException {
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("No valid id provided, please provide it in the format 'tt0090756'.");
		}
		this.id = id;		
		this.url = BASE_URL + id;
		this.doc = doc;
		this.title = obtainTitleFromHtml();
		this.genresSet = obtainGenresFromHtml();
		this.genres = StringUtils.join(genresSet.toArray(), ", ");
		this.rating = obtainRatingFromHtml();
		this.primaryActors = obtainPrimaryActorsFromHtml();
		this.directorsOrCreators = obtainDirectorOrCreatorsFromHtml();
		this.summaryText = obtainSummaryTextFromHtml();
	}		
	
	/**
	 * @return The IMDb id of the current title, in the format 'tt0090756'.
	 */
	public String getId() {
		return id;
	}	
	
	/**
	 * @return The URL to the IMDb page of the current title.
	 */
	public String getUrl() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}

	/**
	 * @return A comma-separated string of all genres of the current title.
	 */
	public String getGenres() {
		return genres;
	}
	
	/**
	 * @return A set of all genres of the current title.
	 */
	public Set<String> getGenresSet() {
		return genresSet;
	}

	/**
	 * @return IMDb rating (out of 10) of the current title.
	 */
	public Double getRating() {
		return rating;
	}
	
	/**
	 * @return Short summary of the title.
	 */
	public String getSummaryText() {
		return summaryText;
	}

	/**
	 * @return The first listed actors of the title (up to 15, usually).
	 */
	public ArrayList<Person> getPrimaryActors() {
		return primaryActors;
	}
	
	/**
	 * @return The director(s) (for movies of episodes) or creators (for series) of the current title.
	 */
	public ArrayList<Person> getDirectorsOrCreators() {
		return directorsOrCreators;
	}
	
	/**
	 * @return 'Director' for movies or episodes, and 'Creator' for series.
	 */
	public abstract String getDirectorFunctionName();
	
	/**
	 * @return Additional info on the current title; for episodes, this contains the name of the series as well as season and episode number.
	 */
	public abstract String getSubTitle();
	

	
	/**
	 * Lookup the title for the current title in its html Document.
 	 * @throws NullPointerException If the title cannot be found, indicating Document is probably not a valid IMDb page.
	 */
	private String obtainTitleFromHtml() throws NullPointerException {
		try {
			return doc.select(".titleBar h1").first().text();			
		} catch (NullPointerException e) {
			System.err.println("Could not parse html page for title " + id);
			throw e;
		}
	}
	
	/**
	 * Lookup the genres for the current title in its html Document
	 */
	private Set<String> obtainGenresFromHtml() {
		// get genres
		Set<String> genres = new HashSet<String>() ;
		
		Elements genreElements = doc.select(".titleBar span.itemprop");		
		if (genreElements != null) {
			for (Element e : genreElements) {
				genres.add(e.text());
			}	
		}
		
		return genres;		
	}

	/**
	 * Lookup the rating for the current title in its html Document.
	 * If no rating is found, null is returned.
	 */
	private Double obtainRatingFromHtml() {
		Double r = null;
		// get rating
		try {
			r = Double.parseDouble(doc.select(".imdbRating span[itemprop=\"ratingValue\"]").first().text());
		} catch (NumberFormatException e) {
			System.err.println("Could not set rating for " + id + " (could not convert element to integer)");
		} catch (NullPointerException e) {
			System.err.println("Could not set rating for " + id + " (could not find element)");
		}
		return r;
	}
	
	/**
	 * Lookup the primary actors (the first 15 listed) for the current title in its html Document.
	 */
	private ArrayList<Person> obtainPrimaryActorsFromHtml() {
		ArrayList<Person> actors = new ArrayList<Person>();
		
		Elements actorsElements = doc.select(".cast_list td[itemprop=\"actor\"] a");
		if (actorsElements != null) {
			for (Element actor : actorsElements) {		
				try {
					String id = Person.getIdFromUrl(actor.attr("href"));
					String name = actor.text();
					actors.add(new Person(id, name));
				} catch (IllegalArgumentException e) {
					System.err.println("error while addign actor to " + id + "; invalid id or name encountered");
				}
			}	
		}
		
		return actors;
	}
	
	/**
	 * Lookup the director(s) of the current title (movie or episode) in its html Document.
	 */
	protected ArrayList<Person> obtainDirectorOrCreatorsFromHtml() {
		ArrayList<Person> directors = new ArrayList<Person>();
		Elements directorElements = doc.select(".credit_summary_item:eq(1) span[itemprop=\"director\"] a, .credit_summary_item:eq(1) span[itemprop=\"creator\"] a");		
		for (Element directorElement : directorElements) {		
			try {
				String directorId = Person.getIdFromUrl(directorElement.attr("href"));
				String directorName = directorElement.text();
				directors.add(new Person(directorId, directorName));
			} catch (IllegalArgumentException e) {
				System.err.println("could not set director/creator for " + id + "; invalid id or name encountered");
			}
		}
		return directors;
	} 
	
	/**
	 * Lookup the primary actors (the first 15 listed) for the current title in its html Document.
	 */
	private String obtainSummaryTextFromHtml() {
		return doc.select(".summary_text").first().text();			
	}
}
