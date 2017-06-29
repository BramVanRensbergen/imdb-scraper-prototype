package com.bramvanrensbergen.imdb_demo;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bramvanrensbergen.imdb_demo.resource.Title;

@Controller
public class RequestController {

    @RequestMapping("/request")
    public Request request(@RequestParam(value="title", required=false, defaultValue="") String title, Model model) {
       
    	try {
			Title t = new Title(title);
			model.addAttribute("title", t);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return new Request(title);
    }

}
