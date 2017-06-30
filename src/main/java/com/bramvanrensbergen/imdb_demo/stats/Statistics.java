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

	private static final int ACTORS_TO_DISPLAY = 20;

	/**
	 * k Actor's ID, v some stats we'll compute.
	 */
	private List<ActorStatRow> actorStats;
	
	private int nTitles;
	private int nMovies = 0;
	private int nEpisodes = 0;
	private int nSeries = 0;
	
	/**
	 * Generate a set of statistics for the indicated titles.
	 */
	public Statistics(List<Title> titles) {	 		
		nTitles = titles.size();
		
		Map<String, ActorStatRow> actorStatsMap = new HashMap<String, ActorStatRow>();
		
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
					actorStatsMap.put(key, new ActorStatRow(p));
				}
				
				actorStatsMap.get(key).nbOfOccurrences++;
				actorStatsMap.get(key).ratingSum += t.getRating();				
				actorStatsMap.get(key).userRatingSum += t.getUserRating();
			}			
		}
		
		// sort actors by occurrence
		actorStats = new ArrayList<ActorStatRow>(actorStatsMap.values());		
		Collections.sort(actorStats, new Comparator<ActorStatRow>() {
	        @Override public int compare(ActorStatRow a1, ActorStatRow a2) {
	            return a2.nbOfOccurrences - a1.nbOfOccurrences; // Ascending
	        }
	    });
		
		// transform sums to averages
		for (ActorStatRow s : actorStats) {
			s.ratingAvg = (double) s.ratingSum / s.nbOfOccurrences;
			s.userRatingAvg = (double) s.userRatingSum / s.nbOfOccurrences;
		}
		
	}

	/**
	 * @return Stats for the {@code ACTORS_TO_DISPLAY} that were cast most often in the indicated titles.
	 */
	public List<ActorStatRow> getActorStats() {
		return actorStats.subList(0, ACTORS_TO_DISPLAY);
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
