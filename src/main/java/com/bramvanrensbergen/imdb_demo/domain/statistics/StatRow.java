package com.bramvanrensbergen.imdb_demo.domain.statistics;

import java.util.ArrayList;
import java.util.List;

import com.bramvanrensbergen.imdb_demo.domain.Title;

/**
 * Object representing a row of statistics, containing a name (e.g., actor, director, or genre), 
 * url (e.g., to an actors imdb page), how often that entity occurred in the dataset, the average
 * imdb rating of the titles in which the entity occurred, and the average score of the current user for
 * those titles (if working from exported ratings)
 * @author Bram Van Rensbergen
 */
public class StatRow implements Comparable<StatRow>{

	protected String name;
	protected String url;
	
	protected int nbOfOccurrences;
	
	private List<Double> ratings;
	private List<Double> userRatings;
	private List<Integer> runtimesInMinutes;
	private List<Integer> yearsOfRelease;
		
	public StatRow() {		
		name = null;
		url = null;
		nbOfOccurrences = 0;
		ratings = new ArrayList<Double>();
		userRatings = new ArrayList<Double>();
		runtimesInMinutes = new ArrayList<Integer>();
		yearsOfRelease = new ArrayList<Integer>();
	}
	
	public StatRow(String name) {		
		this();
		this.name = name;
	}
	
	public StatRow(String name, String url) {		
		this(name);
		this.url = url;
	}
	
	/**
	 * Add statistics for the indicated title to this row. 
	 * <br>Increments nbOfOccurrences, and adds the title's rating, user rating, runtime, and year 
	 * of release to the row's stats (null entries are skipped).
	 */
	public void addTitle(Title t) {
		if (t == null) {
			return;
		}
		
		nbOfOccurrences++;
		
		Double rating = t.getRating();
		Double userRating = t.getUserRating();
		Integer runtimeMinutes = t.getRuntimeMinutes();
		Integer yearOfRelease = t.getYearOfRelease();
		
		if (rating != null) {
			ratings.add(rating);			
		}
		
		if (userRating != null) {
			userRatings.add(userRating);			
		}
		
		if (runtimeMinutes != null) {
			runtimesInMinutes.add(runtimeMinutes);			
		}
		
		if (yearOfRelease != null) {
			yearsOfRelease.add(yearOfRelease);			
		}
	}

	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}

	public int getNbOfOccurrences() {
		return nbOfOccurrences;
	}

	/**
	 * @return The average idmb-rating of all analyzed titles in which this entity occurred (e.g. person was cast), 
	 * or null if no ratings were found. 
	 */
	public String getAvgRating() {
		return MathUtil.getFormattedDouble(MathUtil.computeAvgOfDoubleList(ratings));
	}

	/**
	 * @return The average rating of the current user of all analyzed titles in which this entity occurred (e.g. person was cast), 
	 * or null if no ratings were found.
	 */
	public String getAvgUserRating() {
		return MathUtil.getFormattedDouble(MathUtil.computeAvgOfDoubleList(userRatings));
	}
	
	/**
	 * @return The average runtime of all analyzed titles in which this entity occurred (e.g. person was cast), 
	 * in the format '1h 55min', '30min', or '2h'; or null if no runtimes were found.
	 */
	public String getAvgRuntime() {
		return MathUtil.getAverageRuntimeString(runtimesInMinutes);	
	}
	
	/**
	 * @return The average runtime of all analyzed titles in which this entity occurred (e.g. person was cast), 
	 * in the format '1h 55min', '30min', or '2h'; or null if no runtimes were found.
	 */
	public Integer getAvgYearOfRelease() {
		return MathUtil.computeAvgOfIntegerList(yearsOfRelease);	
	}

	@Override
	public int compareTo(StatRow o) {
		return o.nbOfOccurrences - nbOfOccurrences;
	}
	
	
}
