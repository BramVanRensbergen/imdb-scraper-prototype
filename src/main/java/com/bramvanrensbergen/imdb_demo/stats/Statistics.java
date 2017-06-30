package com.bramvanrensbergen.imdb_demo.stats;

import java.util.ArrayList;
import java.util.Collection;
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

public class Statistics {

	/**
	 * k Actor's ID, v some stats we'll compute.
	 */
	private List<ActorStatRow> actorStats;
	
	private int nTitles;
	private int nMovies = 0;
	private int nEpisodes = 0;
	private int nSeries = 0;
	
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

	public Collection<ActorStatRow> getActorStats() {
		return actorStats;
	}

	public int getnTitles() {
		return nTitles;
	}

	public int getnMovies() {
		return nMovies;
	}

	public int getnEpisodes() {
		return nEpisodes;
	}

	public int getnSeries() {
		return nSeries;
	}
	
	
}
