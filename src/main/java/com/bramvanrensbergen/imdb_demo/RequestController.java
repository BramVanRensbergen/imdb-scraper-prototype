package com.bramvanrensbergen.imdb_demo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bramvanrensbergen.imdb_demo.stats.Statistics;
import com.bramvanrensbergen.imdb_demo.titel.TitleLookupService;


@Controller
public class RequestController {

	@Resource 
	private TitleLookupService titleLookupService;
		
	/**
	 * Redirect to a page with some statistics for all provided ids.
	 * <br>URL should contain list of ids, separated by space, comma, plus, or '%20'. 
	 */
    @RequestMapping("/")
    public String showIndex() throws IOException {      	
        return "index";
    }
	
	/**
	 * Redirect to a page with some statistics for all provided ids.
	 * <br>URL should contain list of ids, separated by space, comma, plus, or '%20'. 
	 */
    @RequestMapping("/title/{ids}")
    public String requestStatsFromUrl(@PathVariable("ids") String ids, Model model) throws IOException {  		
    	model.addAttribute("stats", new Statistics(titleLookupService.createTitlesFromSingleLineOfIds(ids)));	
        return "stats";
    }
    
    /**
	 * Redirect to a page with some statistics for all provided ids.
	 * <br>The 'titles' parameter should contain a number of titles or titleIds, each on a separate line.
	 */
    @RequestMapping("/stats")
    public String requestStatsFromText(@RequestParam(value="titles") String titles, Model model) throws IOException {
    	model.addAttribute("stats", new Statistics(titleLookupService.createTitlesFromText(titles)));	
        return "stats";
    }
    
    /**
	 * Redirect to a page with some statistics for all provided ids.
	 * <br>The 'titles' parameter should contain a number of titles or titleIds, each on a separate line.
	 */
    @RequestMapping("/exportedRatings")
    public String requestStatsFromExportedRatings(@RequestParam(value="exportedRatings") String exportedRatings, Model model) throws IOException {
    	model.addAttribute("stats", new Statistics(titleLookupService.createTitlesFromExportedRatings(exportedRatings)));	
        return "stats";
    }

    @RequestMapping("/sample")
    public String requestSampleStats(Model model) throws IOException, InterruptedException, ExecutionException {     	
    	model.addAttribute("stats", new Statistics(titleLookupService.createTitlesFromSampleData()));
        return "stats";
    }
    
}
