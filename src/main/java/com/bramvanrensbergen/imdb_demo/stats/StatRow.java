package com.bramvanrensbergen.imdb_demo.stats;

import java.util.List;

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
	protected double ratingSum;
	protected int userRatingSum;
	protected Double ratingAvg;
	protected Double userRatingAvg; 
	
	// unreleased titles have no ratings, so we need a separate count to obtain accurate averages
	protected int nbOfOccurrencesWithRatings = 0;
	
	public StatRow(String name) {		
		this.name = name;
		
		url = null;
		nbOfOccurrences = 0;
		ratingSum = 0;
		userRatingSum = 0;
	}
	
	public StatRow(String name, String url) {		
		this.name = name;
		this.url = url;

		nbOfOccurrences = 0;
		ratingSum = 0;
		userRatingSum = 0;
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

	public double getRatingSum() {
		return ratingSum;
	}

	public int getUserRatingSum() {
		return userRatingSum;
	}

	/**
	 * @return The average idmb-rating of all analyzed titles in which this entity occurred (e.g. person was cast), 
	 * or null if no ratings were found. 
	 */
	public String getRatingAvg() {		
		if (ratingAvg == null) {
			return null;
		}
		return String.format( "%.2f", ratingAvg);
	}

	/**
	 * @return The average rating of the current use of all analyzed titles in which this entity occurred (e.g. person was cast), 
	 * or null if no ratings were found.
	 */
	public String getUserRatingAvg() {
		if (userRatingAvg == null || userRatingAvg == 0) {
			return null;
		}
		return String.format( "%.2f", userRatingAvg);
	}
	
	/**
	 * Increment the running total (or do nothing, if increment is null).
	 */
	protected void incrementRatingSum(Double increment) {
		if (increment != null) {
			ratingSum += increment;
			nbOfOccurrencesWithRatings++;			
		}
	}
	
	protected void incrementUserRatingSum(Double increment) {
		if (increment != null) {
			userRatingSum += increment;
		}
	}
	
	protected static void calculateAndSetAllAverages(List<StatRow> stats) {
		for (StatRow p : stats) {
			if (p.nbOfOccurrencesWithRatings > 0) {
				p.ratingAvg = (double) p.ratingSum / p.nbOfOccurrencesWithRatings;
				p.userRatingAvg = (double) p.userRatingSum / p.nbOfOccurrencesWithRatings;				
			} 
		}
	}

	@Override
	public int compareTo(StatRow o) {
		return o.nbOfOccurrences - nbOfOccurrences;
	}
}
