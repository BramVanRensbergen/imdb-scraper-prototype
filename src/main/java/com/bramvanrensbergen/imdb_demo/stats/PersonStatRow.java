package com.bramvanrensbergen.imdb_demo.stats;

import java.util.List;

import com.bramvanrensbergen.imdb_demo.titel.Person;

/**
 * Object representing statistics generated for a specific actor. Created in the Statistics class.
 * @author Bram Van Rensbergen
 */
public class PersonStatRow {

	protected Person actor;
	protected int nbOfOccurrences = 0;
	protected double ratingSum = 0;
	protected int userRatingSum = 0;
	protected Double ratingAvg;
	protected Double userRatingAvg; 
	
	// unreleased titles have no ratings, so we need a separate count to obtain accurate averages
	protected int nbOfOccurrencesWithRatings = 0;
	
	public PersonStatRow(Person actor) {
		this.actor = actor;
		
		nbOfOccurrences = 0;
		ratingSum = 0;
		userRatingSum = 0;
	}

	public Person getActor() {
		return actor;
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
	 * @return The average idmb-rating of all analyzed titles in which this person was cast, 
	 * or null if no ratings were found.
	 */
	public String getRatingAvg() {		
		if (ratingAvg == null) {
			return null;
		}
		return String.format( "%.2f", ratingAvg);
	}

	/**
	 * @return The average rating of the current use of all analyzed titles in which this person was cast, 
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
	
	protected static void calculateAndSetAllAverages(List<PersonStatRow> stats) {
		for (PersonStatRow p : stats) {
			if (p.nbOfOccurrencesWithRatings > 0) {
				p.ratingAvg = (double) p.ratingSum / p.nbOfOccurrencesWithRatings;
				p.userRatingAvg = (double) p.userRatingSum / p.nbOfOccurrencesWithRatings;				
			} 
		}
	}
}
