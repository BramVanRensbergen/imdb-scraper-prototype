package com.bramvanrensbergen.imdb_demo.stats;

import java.util.List;

public abstract class MathUtil {

	/**
	 * @return The indicated double, with up to two decimals; or null, if d is null.
	 */
	static String getFormattedDouble(Double d) {
		if (d == null) {
			return null;
		} 
		
		return String.format( "%.2f", d);
	}	
	
	/**
	 * Convert the indicated description of a title runtime to that runtime in minutes.
	 * @param runtimeDescription in the format '1h 55min', '30min', or '2h' (no other units are used on idmb)
	 * @return The description converted to minutes, or null if runtimeDescription is null
	 */
	public static Integer runtimeDescriptionToMinutes(String runtimeDescription) {
		if (runtimeDescription == null) {
			return null;
		}
		
		int rt = 0;
			
			int hourIndex = runtimeDescription.indexOf('h');		
			if (hourIndex != -1) {
				int hours = Integer.parseInt(runtimeDescription.substring(0, hourIndex));
				rt = hours * 60;				
			}			
			
			// if two-part runtime i.e. if both hour and mins are present, strip hours
			if (hourIndex != -1 && runtimeDescription.indexOf("min") != -1) {
				runtimeDescription = runtimeDescription.substring(hourIndex + 2, runtimeDescription.length());
				// + 2: include the 'h' as well as the space
			}
			
			int minuteIndex = runtimeDescription.indexOf("min");		
			if (minuteIndex != -1) {				
				int minutes = Integer.parseInt(runtimeDescription.substring(0, minuteIndex));
				rt += minutes;				
			}				
		
		return rt;
	}
	
	/**
	 * Calculate the average of a list of runtimes
	 * @param runtimesInMinutes
	 * @return Average runtime, in the format '1h 55min', '30min', or '2h'
	 */
	static String getAverageRuntimeString(List<Integer> runtimesInMinutes) {
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
	 * @return average of list, or null, if list is empty
	 */
	static Double computeAvgOfDoubleList(List<Double> numbers) {
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
	static Integer computeAvgOfIntegerList(List<Integer> numbers) {
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
