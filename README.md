IMDb Analyzer
==========================
Author: Bram Van Rensbergen <mail@bramvanrensbergen.com>

A small tool I created to gain some experience with the Java Spring framework.

It allows you to enter any number of titles of movies, tv-series, or episodes,
and looks up some data for these titles:
 * Basic metadata for each title
 * Which actors were cast most often in these titles, and the average rating, runtime, and year of the titles in which they were cast
 * Which directors directed most often in these titles, and the average rating, runtime, and year of the titles in which they directed
 * Which genres occurred most often among these titles, and the average rating, runtime, and year of these titles
	
You can do also input your exported IMDb ratings, in which case your own rating is included in the statistics.

Note that the lookup happens synchronously, so this may not work if you look up a large amount of ratings at once. 
I'll try to get around to looking them up asynchronously...

To run, download the project and build using maven (e.g., './mvnw spring-boot:run'), then visit the index page in your browser (e.g., localhost:8080).

Note: I'm well aware that Java is not the ideal tool for this functionality, I was just looking for a simple web-service to play around with Spring :)