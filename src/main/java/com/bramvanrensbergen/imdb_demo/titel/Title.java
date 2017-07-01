package com.bramvanrensbergen.imdb_demo.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;

/**
 * Object representing a movie, series, or episode of a series.
 * @author Bram Van Rensbergen 
 */
public abstract class Title {
	private static final String BASE_URL = "http://www.imdb.com/title/";
	
	private static final String SAMPLE_RATINGS_FILE = "static/sample_ratings.csv";
	
	/**
	 * At most this many sample ratings will be taken from {@code SAMPLE_RATINGS_FILE}
	 * <br>Set to 0 to select all.
	 */
	private static final int N_SAMPLES_MAX = 100;
	
	/**
	 * When a list of titles is passed to the web service, they can be separated by a number of characters; this regex string splits them apart.
	 */
	private static final String ID_SEPARATOR_REGEX = "%20| |\\+|,";
	
	private static final String NEWLINE_SEPARATOR_REGEX = "[\r\n]+";
		
	protected final String id;
	
	protected Document doc;	
	
	private String url;
	
	private String title;
	
	private String genres;
	
	private Set<String> genresSet;
		
	private Double rating;	
	
	private String summaryText;
		
	private Double userRating = null;

	/**
	 * The director(s) (for episodes and movies) or creator(s) (for series) of the current title.
	 */
	private ArrayList<Person> directorsOrCreators;

	// currently max. 15
	private ArrayList<Person> primaryActors;

	/**
	 * Get the imdb id contained in the indicated url.
	 * @param url containing a title's id, e.g. /title/tt0000186?ref_=tt_ov_dr
	 * @return the id, e.g. tt0000186
	 */
	public static String getIdFromUrl(String url) {
		return url.split("/title/")[1].split("\\?")[0];
	}
	
	/**
	 * Get the imdb-id of the best match (accordign to IMDb) for the indicated title.
	 * @param title Title of a movie/episode/series to look up
	 * @return IMDb id of the first match for that title (null if no match could be found)
	 */
	public static String getBestMatchForTitle(String title) {		
		if (title == null) {
			return null;
		}
		
		String id = null;
		
		String url = "http://www.imdb.com/find?q=" + title.trim() + "&s=tt";		
		
		try {			
			Document doc = Jsoup.connect(url).get();
			Element e = doc.select(".findList a").first();
			id = getIdFromUrl(e.attr("href"));
		} catch (IOException e) {
			System.err.println("Could not reach imdb page at " + url);
		} catch (NullPointerException e) {
			System.err.println("Could not find any results at " + url);
		}
	
		return id;
	}
	
	/**
	 * Create a list with Title objects for each titleId in the String; ids can be separated by space, comma, plus, or '%20'.
	 * <br>Invalid IDs are skipped.
	 * @param title_ids String containing any number of titleIds, separated by space, comma, plus, or '%20'.
	 * @return A list of Title objects corresponding to those ids.
	 */
	public static List<Title> createTitlesFromSingleLineOfIds(String title_ids) {
		return createTitles(title_ids.split(ID_SEPARATOR_REGEX));
	}
	
	/**
	 * Create a list with Title objects for each line containing a valid title or titleId in the indicated String.
	 * @param titles String containing any number of lines, each of which should hold a single title or titleId.
	 * @return A list of Title objects corresponding to those titles or ids.
	 */
	public static List<Title> createTitlesFromText(String titles) {
		return createTitles(titles.split(NEWLINE_SEPARATOR_REGEX));
	}
	
	public static List<Title> createTitlesFromSampleData() throws FileNotFoundException, IOException {
		ArrayList<Title> titlesList = new ArrayList<Title>();

		Reader in = new InputStreamReader(new ClassPathResource(SAMPLE_RATINGS_FILE).getInputStream());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
		
		int titlesCreated = 0;
		
		for (CSVRecord record : records) {
			if (titlesCreated >= N_SAMPLES_MAX && N_SAMPLES_MAX != 0) {
		    	break;
		    }
			
		    String id = record.get("const");
		    String typeString = record.get("Title type");
		    String userRating = record.get("You rated");		    		    
		    
		    if (id != null && !id.isEmpty() && typeString != null && !typeString.isEmpty()) {		    	
		    	Document doc = Jsoup.connect(BASE_URL + id).get();
			    Title t = createTitle(id, doc, typeString);
			    t.userRating = Double.parseDouble(userRating);
			    titlesList.add(t);
			    titlesCreated++;
		    }		    
		}
	
		
		return titlesList;
	}
	
	/**
	 * Create a list with Title objects for each title or titleId in the indicated array
	 * <br>Invalid IDs are skipped.
	 * @param titlesOrIds An array of titles or titleIds
	 * @return List of Title objects corresponding to those ids.
	 */
	private static List<Title> createTitles(String[] titlesOrIds) {
		ArrayList<Title> titlesList = new ArrayList<Title>();
		
		Set<String> alreadyAddedTitles = new HashSet<String>();				
		
		for (String titleOrId : titlesOrIds) {			
			try {
				if (titleOrId == null) {
					System.out.println("null title/id passed '" + titleOrId + "', skipping");
					continue;
				}
				
				// to continue, we need the imdb id, which we have to look up in case titleOrId reflects a title
				String id;				
				if (titleOrId.startsWith("tt")) {
					id = titleOrId;
				} else {
					id = getBestMatchForTitle(titleOrId);
				}
				
				if (id == null) {
					System.out.println("no valid id found for title '" + titleOrId + "', skipping");
					continue;
				}
				
				
				// make sure to avoid any duplicates
				if (alreadyAddedTitles.contains(id)) {
					System.out.println("skipping duplicate " + id);
					continue;
				}
				alreadyAddedTitles.add(id);
				
				Document doc = Jsoup.connect(BASE_URL + id).get();
				Element e = doc.select(".titleBar .subtext a:last-child").first();
				
				if (e == null) {
					System.err.println("Could not obtain type of title for " + id + ", skipping");
					continue;
				}
				
				String titleTypeDesc = e.text();			
				titlesList.add(createTitle(id, doc, titleTypeDesc));
			} catch (IOException e) {
				System.err.println("Could not obtain find imdb page for '" + titleOrId + "', skipping");
			}
			
		}
		
		return titlesList;
	}
	
	private static Title createTitle(String id, Document doc, String titleTypeDescription) throws IOException {
		Title t;
		
		// find out what type of title this is
		// not exactly elegant but hey...
		if (titleTypeDescription.contains("Series") ) {
			t = new Series(id, doc);
		} else if (titleTypeDescription.contains("Episode")) {
			t = new Episode(id, doc);
		} else {
			t = new Movie(id, doc);
		}			
		
		return t;
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
	
	protected Title(String id, Document doc, double userRating) throws IOException {
		this(id, doc);
		this.userRating = userRating;
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
	 * @return IMDb rating (out of 10) of the current title, or null if no rating was found.
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
	 * @return The current user's rating of the title; only used when analyzing exported ratings;
	 * or null, if no such rating could be found.
	 */
	public Double getUserRating() {
		return userRating;
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
