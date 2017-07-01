package com.bramvanrensbergen.imdb_demo.titel;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 * Service to look up titles on IMDb, based on either imdb-ids or on title.
 * @author Bram Van Rensbergen 
 */
@Service
public class TitleLookupService {

	private static final String SAMPLE_RATINGS_FILE = "static/sample_ratings.csv";

	/**
	 * At most this many sample ratings will be taken from {@code SAMPLE_RATINGS_FILE}
	 * <br>Set to 0 to select all.
	 */
	private static final int N_SAMPLES_MAX = 10;
	
	/**
	 * When a list of titles is passed to the web service, they can be separated by a number of characters; this regex string splits them apart.
	 */
	private static final String ID_SEPARATOR_REGEX = "%20| |\\+|,";
	
	private static final String NEWLINE_SEPARATOR_REGEX = "[\r\n]+";
	
	/**
	 * Get the imdb-id of the best match (accordign to IMDb) for the indicated title.
	 * @param title Title of a movie/episode/series to look up
	 * @return IMDb id of the first match for that title (null if no match could be found)
	 */
	public String getBestMatchForTitle(String title) {		
		if (title == null) {
			return null;
		}
		
		String id = null;
		
		String url = "http://www.imdb.com/find?q=" + title.trim() + "&s=tt";		
		
		try {			
			Document doc = Jsoup.connect(url).get();
			Element e = doc.select(".findList a").first();
			id = Title.getIdFromUrl(e.attr("href"));
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
	public List<Title> createTitlesFromSingleLineOfIds(String title_ids) {
		return createTitles(title_ids.split(ID_SEPARATOR_REGEX));
	}
	
	/**
	 * Create a list with Title objects for each line containing a valid title or titleId in the indicated String.
	 * @param titles String containing any number of lines, each of which should hold a single title or titleId.
	 * @return A list of Title objects corresponding to those titles or ids.
	 */
	public List<Title> createTitlesFromText(String titles) {
		return createTitles(titles.split(NEWLINE_SEPARATOR_REGEX));
	}
	
	/**
	 * Create a list of Title objects of sample data (part of my own exported ratings).
	 * @return A list of Title objects from sample data.
	 */
	public List<Title> createTitlesFromSampleData() throws FileNotFoundException, IOException {
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
		    	Document doc = Jsoup.connect(Title.BASE_URL + id).get();
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
	private List<Title> createTitles(String[] titlesOrIds) {
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
				
				Document doc = Jsoup.connect(Title.BASE_URL + id).get();
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
	
	private Title createTitle(String id, Document doc, String titleTypeDescription) throws IOException {
		Title t;
		
		// find out what type of title this is
		// not exactly elegant but hey...
		// works for both description scraped from web, and description present in exported ratings
		if (titleTypeDescription.contains("Series") ) {
			t = new Series(id, doc);
		} else if (titleTypeDescription.contains("Episode")) {
			t = new Episode(id, doc);
		} else {
			t = new Movie(id, doc);
		}			
		
		return t;
	}
	
}
