package com.bramvanrensbergen.imdb_demo.stats;

import java.util.ArrayList;
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
	
	private List<Double> ratings;
	private List<Double> userRatings;
	private List<Integer> runtimesInMinutes;
		
	public StatRow(String name) {		
		this.name = name;
		
		url = null;
		nbOfOccurrences = 0;
		ratings = new ArrayList<Double>();
		userRatings = new ArrayList<Double>();
		runtimesInMinutes = new ArrayList<Integer>();
	}
	
	public StatRow(String name, String url) {		
		this(name);
		this.url = url;
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
	public String getRatingAvg() {
		Double avg = computeAvgOfDoubleList(ratings);
		
		if (avg == null) {
			return null;
		}
		
		return String.format( "%.2f", avg);
	}

	/**
	 * @return The average rating of the current user of all analyzed titles in which this entity occurred (e.g. person was cast), 
	 * or null if no ratings were found.
	 */
	public String getUserRatingAvg() {
		Double avg = computeAvgOfDoubleList(userRatings);
		
		if (avg == null) {
			return null;
		}
		
		return String.format( "%.2f", avg);
	}
	
	/**
	 * @return The average runtime of all analyzed titles in which this entity occurred (e.g. person was cast), 
	 * in the format '1h 55min', '30min', or '2h'; or null if no runtimes were found.
	 */
	public String getRuntimeAvg() {
		Integer minuteAvg = computeAvgOfIntegerList(runtimesInMinutes);
		
		if (minuteAvg == null) {
			return null;
		}
		
		if (minuteAvg < 60) {
			return minuteAvg + "min";
		}
		
		int hours = minuteAvg / 60;
		int minutesLeft = minuteAvg - hours * 60;
		
		if (minutesLeft > 0) {
			return hours + "h " + minutesLeft + "min"; 
		} else {
			return hours + "h";
		}		
	}
	
	/**
	 * Add the indicated rating to the list of ratings for this row (or do nothing, if increment is null).
	 */
	protected void addRating(Double increment) {
		if (increment != null) {
			ratings.add(increment);			
		}
	}
	
	/**
	 * Add the indicated user rating to the list of ratings for this row (or do nothing, if increment is null).
	 */
	protected void addUserRating(Double increment) {
		if (increment != null) {
			userRatings.add(increment);	
		}
	}

	/**
	 * Add the indicated runtime to the list of runtimes for this row (or do nothing, if param is null or cannot be parsed).
	 * @param runtimeString in the format '1h 55min', '30min', or '2h' (no other units are used on idmb)
	 */
	protected void addRuntime(String runtimeString) {		
		int rt = 0;
		
		if (runtimeString != null) {
			
			int hourIndex = runtimeString.indexOf('h');		
			if (hourIndex != -1) {
				int hours = Integer.parseInt(runtimeString.substring(0, hourIndex));
				rt = hours * 60;				
			}			
			
			// if two-part runtime i.e. if both hour and mins are present, strip hours
			if (hourIndex != -1 && runtimeString.indexOf("min") != -1) {
				runtimeString = runtimeString.substring(hourIndex + 2, runtimeString.length());
				// + 2: include the 'h' as well as the space
			}
			
			int minuteIndex = runtimeString.indexOf("min");		
			if (minuteIndex != -1) {				
				int minutes = Integer.parseInt(runtimeString.substring(0, minuteIndex));
				rt += minutes;				
			}				
		}
		
		runtimesInMinutes.add(rt);	
	}

	@Override
	public int compareTo(StatRow o) {
		return o.nbOfOccurrences - nbOfOccurrences;
	}
	
	/**
	 * @return average of list, or null, if list is empty
	 */
	private Double computeAvgOfDoubleList(List<Double> numbers) {
		if (numbers.isEmpty()) {
			return null;
		}
		
		double count = 0;
		for (Double n : numbers) {
			count = count + n.doubleValue();
		}
		return (double) count / numbers.size();
	}
	
	/**
	 * @return average of list, rounded to the nearest integer, or null, if list is empty
	 */
	private Integer computeAvgOfIntegerList(List<Integer> numbers) {
		if (numbers.isEmpty()) {
			return null;
		}
		
		int count = 0;
		for (int n : numbers) {
			count += n;
		}
		return (int) count / numbers.size();
	}
}
