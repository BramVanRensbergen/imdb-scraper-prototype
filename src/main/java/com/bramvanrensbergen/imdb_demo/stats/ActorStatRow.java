package com.bramvanrensbergen.imdb_demo.stats;

import com.bramvanrensbergen.imdb_demo.data.Person;

public class ActorStatRow {

	protected Person actor;
	protected int nbOfOccurrences = 0;
	protected double ratingSum = 0;
	protected int userRatingSum = 0;
	protected double ratingAvg;
	protected double userRatingAvg; 
	
	public ActorStatRow(Person actor) {
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

	public double getRatingAvg() {
		return ratingAvg;
	}

	public double getUserRatingAvg() {
		return userRatingAvg;
	}
	
}
