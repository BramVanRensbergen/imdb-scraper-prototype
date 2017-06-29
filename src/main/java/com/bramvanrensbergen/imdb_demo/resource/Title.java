package com.bramvanrensbergen.imdb_demo.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Title {
	private static final String BASE_URL = "http://www.imdb.com/title/";
	
	private final String id;
	
	private String url;

	private String titleAndYear;
	
	private String genres;
	
	private Set<String> genresSet;
	
	private Integer year;
	
	private Double rating;
	
	private ArrayList<Person> directors;
	
	// currently 15
	private ArrayList<Person> primaryActors;

	public Title(String id) throws IOException {
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("No valid id provided, please provide it in the format 'tt0090756'.");
		}
		
		this.id = id;		
		this.url = BASE_URL + id;
		
		obtainAndParseHtml();
	}	
	
	/**
	 * Request the imdb page for the current movie / episode, look up and set the title, 
	 * and look up and set, if found, the  year of publication, genres, rating, director, and the first {@code MAX_ACTORS} actors.
	 * 
	 * Only title is guaranteed to be set; if for some reason any of the other values cannot be obtained from the html-page, their 
	 * corresponding variable remains null.
	 * 
	 * @throws IOException If the imdb page cannot be reached.
	 * @throws NullPointerException If the imdb page is not valid (when no title element can be found).
	 */	
	private void obtainAndParseHtml() throws IOException {
		Document doc = Jsoup.connect(url).get();
		
		// format: Blue Velvet (1986)
		// if the element is not found for whatever reason, will throw NullPointerException, which should be handled gracefully
		try {
			this.titleAndYear = doc.select(".titleBar h1").first().text();			
		} catch (NullPointerException e) {
			System.err.println("Could not parse html page for title " + id);
			throw e;
		}
		
		// get year of publication
		try {
			this.year = Integer.parseInt(doc.select("#titleYear a").first().text());
		} catch (NumberFormatException e) {
			System.err.println("Could not set year for " + id + " (could not convert element to integer)");
		} catch (NullPointerException e) {
			System.err.println("Could not set year for " + id + " (could not find element)");
		}

		// get genres
		this.genresSet = new HashSet<String>() ;
		Elements genreElements = doc.select(".titleBar span.itemprop");
		
		for (Element e : genreElements) {
			this.genresSet.add(e.text());
		}		
		genres = StringUtils.join(genresSet.toArray(), ", ");
		
		// get rating
		try {
			this.rating = Double.parseDouble(doc.select(".imdbRating span[itemprop=\"ratingValue\"]").first().text());
		} catch (NumberFormatException e) {
			System.err.println("Could not set rating for " + id + " (could not convert element to integer)");
		} catch (NullPointerException e) {
			System.err.println("Could not set rating for " + id + " (could not find element)");
		}
				
		// get director
		directors = new ArrayList<Person>();
		Elements directorElements = doc.select(".credit_summary_item span[itemprop=\"director\"] a");		
		for (Element directorElement : directorElements) {		
			try {
				String directorId = Person.getIdFromUrl(directorElement.attr("href"));
				String directorName = directorElement.text();
				directors.add(new Person(directorId, directorName));
			} catch (IllegalArgumentException e) {
				System.err.println("could not set director for " + id + "; invalid id or name encountered");
			}
		}		
		
		// get primary actors
		primaryActors = new ArrayList<Person>();
		Elements actorsElements = doc.select(".cast_list td[itemprop=\"actor\"] a");
		for (Element actor : actorsElements) {		
			try {
				String id = Person.getIdFromUrl(actor.attr("href"));
				String name = actor.text();
				primaryActors.add(new Person(id, name));
			} catch (IllegalArgumentException e) {
				System.err.println("error while addign actor to " + id + "; invalid id or name encountered");
			}
		}
		
	}
	
	public String getId() {
		return id;
	}	
	
	public String getUrl() {
		return url;
	}

	public String getTitleAndYear() {
		return titleAndYear;
	}

	public String getGenres() {
		return genres;
	}
	
	public Set<String> getGenresSet() {
		return genresSet;
	}
	
	public Integer getYear() {
		return year;
	}

	public Double getRating() {
		return rating;
	}

	public ArrayList<Person> getDirectors() {
		return directors;
	}

	public ArrayList<Person> getPrimaryActors() {
		return primaryActors;
	}
	
}
