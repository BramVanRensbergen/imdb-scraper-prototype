package com.bramvanrensbergen.imdb_demo.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bramvanrensbergen.imdb_demo.data.Episode;
import com.bramvanrensbergen.imdb_demo.data.Movie;
import com.bramvanrensbergen.imdb_demo.data.Person;
import com.bramvanrensbergen.imdb_demo.data.Series;
import com.bramvanrensbergen.imdb_demo.data.Title;

/**
 * Generate a number of basic stats for the indicated titles.
 * @author Bram Van Rensbergen 
 */
public class Statistics {

	private static final int ACTORS_TO_DISPLAY = 50;
	private static final int DIRECTORS_TO_DISPLAY = 20;


	private List<PersonStatRow> actorStats;
	
	private List<PersonStatRow> directorStats;
	
	private int nTitles;
	private int nMovies = 0;
	private int nEpisodes = 0;
	private int nSeries = 0;
		
	/**
	 * Generate a set of statistics for the indicated titles.
	 */
	public Statistics(List<Title> titles) {	 		
		nTitles = titles.size();
		
		Map<String, PersonStatRow> actorStatsMap = new HashMap<String, PersonStatRow>();
		Map<String, PersonStatRow> directorStatsMap = new HashMap<String, PersonStatRow>();
		
		for (Title t : titles) {
			
			if (t instanceof Movie) {
				nMovies++;
			} else if (t instanceof Episode) {
				nEpisodes++;
			} else if (t instanceof Series) {
				nSeries++;
			} 
			
			// actor stats
			for (Person p : t.getPrimaryActors()) {
				String key = p.getId();

				if (!actorStatsMap.containsKey(key)) {
					actorStatsMap.put(key, new PersonStatRow(p));
				}

				actorStatsMap.get(key).nbOfOccurrences++;
				actorStatsMap.get(key).incrementRatingSum(t.getRating());				
				actorStatsMap.get(key).incrementUserRatingSum(t.getUserRating());				
			}		

			// actor stats
			for (Person p : t.getDirectorsOrCreators()) {
				String key = p.getId();

				if (!directorStatsMap.containsKey(key)) {
					directorStatsMap.put(key, new PersonStatRow(p));
				}

				directorStatsMap.get(key).nbOfOccurrences++;
				directorStatsMap.get(key).incrementRatingSum(t.getRating());				
				directorStatsMap.get(key).incrementUserRatingSum(t.getUserRating());	
			}	 
		}

		// sort actors, directors by occurrence
		actorStats = new ArrayList<PersonStatRow>(actorStatsMap.values());		
		directorStats = new ArrayList<PersonStatRow>(directorStatsMap.values());		
		Collections.sort(actorStats, new Comparator<PersonStatRow>() {
			@Override public int compare(PersonStatRow a1, PersonStatRow a2) {
				return a2.nbOfOccurrences - a1.nbOfOccurrences; // Ascending
	        }
	    });
		Collections.sort(directorStats, new Comparator<PersonStatRow>() {
			@Override public int compare(PersonStatRow a1, PersonStatRow a2) {
				return a2.nbOfOccurrences - a1.nbOfOccurrences; // Ascending
	        }
	    });
		
		PersonStatRow.calculateAndSetAllAverages(actorStats);
		PersonStatRow.calculateAndSetAllAverages(directorStats);		
	}

	/**
	 * @return Stats for the {@code ACTORS_TO_DISPLAY} that were cast most often in the indicated titles.
	 */
	public List<PersonStatRow> getActorStats() {
		if (actorStats.size() <= ACTORS_TO_DISPLAY) {
			return actorStats;
		}
		return actorStats.subList(0, ACTORS_TO_DISPLAY);
	}
	
	/**
	 * @return Stats for the {@code DIRECTORS_TO_DISPLAY} that were cast most often in the indicated titles.
	 */
	public List<PersonStatRow> getDirectorStats() {
		if (directorStats.size() <= DIRECTORS_TO_DISPLAY) {
			return directorStats;
		}
		return directorStats.subList(0, DIRECTORS_TO_DISPLAY);
	}

	/**
	 * @return Total number of titles that was analyzed.
	 */
	public int getnTitles() {
		return nTitles;
	}

	/**
	 * @return Total number of movies that was analyzed.
	 */
	public int getnMovies() {
		return nMovies;
	}

	/**
	 * @return Total number of episodes that was analyzed.
	 */
	public int getnEpisodes() {
		return nEpisodes;
	}

	/**
	 * @return Total number of TV-Series that was analyzed.
	 */
	public int getnSeries() {
		return nSeries;
	}
	
	
}
