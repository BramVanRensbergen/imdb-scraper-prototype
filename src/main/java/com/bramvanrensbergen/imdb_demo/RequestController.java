package com.bramvanrensbergen.imdb_demo;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bramvanrensbergen.imdb_demo.data.Title;
import com.bramvanrensbergen.imdb_demo.stats.Statistics;

@Controller
public class RequestController {

	/**
	 * Redirect to a page with some basic info for all provided ids.
	 */
    @RequestMapping("/title/{ids}")
    public String requestInfo(@PathVariable("ids") String ids, Model model) throws IOException {  		
		model.addAttribute("titles", Title.createTitles(ids));			
        return "titles";
    }
    
	/**
	 * Redirect to a page with some basic info for all provided ids.
	 */
    @RequestMapping("/title")
    public String requestInfoGet(@RequestParam(value="ids") String ids, Model model) throws IOException {
		model.addAttribute("titles", Title.createTitles(ids));	
        return "titles";
    }

	/**
	 * Redirect to a page with some statistics for all provided ids.
	 */
    @RequestMapping("/stats/{ids}")
    public String requestStats(@PathVariable("ids") String ids, Model model) throws IOException {       
    	Statistics stats = new Statistics(Title.createTitles(ids));
    	model.addAttribute("stats", stats);	
        return "stats";
    }
    
    /**
	 * Redirect to a page with some statistics for all provided ids.
	 */
    @RequestMapping("/stats")
    public String requestStatsGet(@RequestParam(value="ids") String ids, Model model) throws IOException {
    	Statistics stats = new Statistics(Title.createTitles(ids));
    	model.addAttribute("stats", stats);	
        return "stats";
    }
    
	/**
	 * Redirect to a page with some statistics for all provided ids.
	 */
    @RequestMapping("/sample")
    public String requestSampleStats(Model model) throws IOException {       
    	Statistics stats = new Statistics(Title.createTitlesFromSampleData());
    	model.addAttribute("stats", stats);	
        return "stats";
    }
    
}
