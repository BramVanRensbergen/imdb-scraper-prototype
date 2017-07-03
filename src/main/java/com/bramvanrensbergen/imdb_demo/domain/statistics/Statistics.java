package com.bramvanrensbergen.imdb_demo.domain.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bramvanrensbergen.imdb_demo.domain.Episode;
import com.bramvanrensbergen.imdb_demo.domain.Movie;
import com.bramvanrensbergen.imdb_demo.domain.Person;
import com.bramvanrensbergen.imdb_demo.domain.Series;
import com.bramvanrensbergen.imdb_demo.domain.Title;

/**
 * Generate a number of basic stats for the indicated titles.
 * @author Bram Van Rensbergen 
 */
public class Statistics {	
	private static final int ACTORS_TO_DISPLAY = 50;
	private static final int DIRECTORS_TO_DISPLAY = 20;
	private static final int GENRES_TO_DISPLAY = 10;

	private List<Title> analyzedTitles;
	
	/**
	 * Stats for all titles together.
	 */	
	private StatRow globalStats;
	
	/**
	 * Stats per actor.
	 */
	private List<StatRow> actorStats;
	
	/**
	 * Stats per director.
	 */
	private List<StatRow> directorStats;
	
	/**
	 * Stats per genre.
	 */
	private List<StatRow> genreStats;	
	
	private int nMovies = 0;
	private int nEpisodes = 0;
	private int nSeries = 0;			
	
	/**
	 * Generate a set of statistics for the indicated titles.
	 */
	public Statistics(List<Title> titles) {	 
		analyzedTitles = titles;
		
		
		globalStats = new StatRow();
		Map<String, StatRow> actorStatsMap = new HashMap<String, StatRow>();
		Map<String, StatRow> directorStatsMap = new HashMap<String, StatRow>();
		Map<String, StatRow> genreStatsMap = new HashMap<String, StatRow>();
			
		for (Title t : titles) {
			
			if (t instanceof Movie) {
				nMovies++;
			} else if (t instanceof Episode) {
				nEpisodes++;
			} else if (t instanceof Series) {
				nSeries++;
			} 
			
			globalStats.addTitle(t);
						
			// actor stats
			for (Person p : t.getPrimaryActors()) {
				String key = p.getId();

				if (!actorStatsMap.containsKey(key)) {
					actorStatsMap.put(key, new StatRow(p.getName(), p.getUrl()));
				}

				actorStatsMap.get(key).addTitle(t);
			}		

			// director stats
			for (Person p : t.getDirectorsOrCreators()) {
				String key = p.getId();

				if (!directorStatsMap.containsKey(key)) {
					directorStatsMap.put(key, new StatRow(p.getName(), p.getUrl()));
				}

				directorStatsMap.get(key).addTitle(t);
			}	 
			
			// genres stats
			for (String g : t.getGenresSet()) {
				
				if (!genreStatsMap.containsKey(g)) {
					genreStatsMap.put(g, new StatRow(g));
				}
				
				genreStatsMap.get(g).addTitle(t);
			}
		}

		// sort rows by occurrence, calculate averages
		actorStats = new ArrayList<StatRow>(actorStatsMap.values());		
		directorStats = new ArrayList<StatRow>(directorStatsMap.values());		
		genreStats = new ArrayList<StatRow>(genreStatsMap.values());		
		
		Collections.sort(actorStats);
		Collections.sort(directorStats);
		Collections.sort(genreStats);
	}

	/**
	 * @return A list of the titles on which this analysis is based.
	 */
	public List<Title> getAnalyzedTitles() {
		return analyzedTitles;
	}
	
	/**
	 * @return Stats for all titles together.
	 */
	public StatRow getGlobalStats() {
		return globalStats;
	}

	/**
	 * @return Stats for the {@code ACTORS_TO_DISPLAY} actors that were cast most often in the indicated titles.
	 */
	public List<StatRow> getActorStats() {
		return actorStats.subList(0, Math.min(actorStats.size(), ACTORS_TO_DISPLAY));
	}
	
	/**
	 * @return Stats for the {@code DIRECTORS_TO_DISPLAY} directors that directed most frequently in the indicated titles.
	 */
	public List<StatRow> getDirectorStats() {		
		return directorStats.subList(0, Math.min(directorStats.size(), DIRECTORS_TO_DISPLAY));
	}
	
	/**
	 * @return Stats for the {@code GENRES_TO_DISPLAY} genres that occurred most frequently in the indicated titles.
	 */
	public List<StatRow> getGenreStats() {		
		return genreStats.subList(0, Math.min(genreStats.size(), GENRES_TO_DISPLAY));
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
