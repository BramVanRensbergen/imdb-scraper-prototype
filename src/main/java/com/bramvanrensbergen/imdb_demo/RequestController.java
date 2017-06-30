package com.bramvanrensbergen.imdb_demo;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bramvanrensbergen.imdb_demo.data.Title;

@Controller
public class RequestController {

    @RequestMapping("/title/{ids}")
    public String requestInfo(@PathVariable("ids") String ids, Model model) throws IOException {       
		
		List<Title> titles = Title.createTitles(ids); 
		
		model.addAttribute("titles", titles);			

        return "titles";
    }

    @RequestMapping("/stats/{ids}")
    public String request(@PathVariable("ids") String ids, Model model) throws IOException {       	

        return "stats";
    }
    
}
