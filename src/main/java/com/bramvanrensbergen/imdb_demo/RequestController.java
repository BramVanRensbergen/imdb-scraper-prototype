package com.bramvanrensbergen.imdb_demo;

import java.io.IOException;
import java.util.List;

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
    	List<Title> titles = Title.createTitlesFromSingleLineOfIds(ids);
    	Statistics stats = new Statistics(titles);
    	model.addAttribute("stats", stats);	
    	model.addAttribute("titles", titles);	
        return "stats";
    }
    /**
	 * Redirect to a page with some statistics for all provided ids.
	 * <br>The 'titles' parameter should contain a number of titles or titleIds, each on a separate line.
	 */
    @RequestMapping("/stats")
    public String requestStatsFromText(@RequestParam(value="titles") String titles, Model model) throws IOException {
    	List<Title> titleList = Title.createTitlesFromText(titles);    	
    	Statistics stats = new Statistics(titleList);
    	model.addAttribute("stats", stats);	
    	model.addAttribute("titles", titleList);	
        return "stats";
//        return "redirect:/";
    }

    @RequestMapping("/sample")
    public String requestSampleStats(Model model) throws IOException { 
    	Statistics stats = new Statistics(Title.createTitlesFromSampleData());
    	model.addAttribute("stats", stats);	
        return "stats";
    }
    
}
