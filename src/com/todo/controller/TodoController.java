package com.todo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TodoController  
{
	@RequestMapping(value="/home", method = RequestMethod.GET)
	public String homePage(HttpServletRequest request, HttpServletResponse response) {
		
		return "home";
	}
	
	@RequestMapping("/")
	  public String HelloWorld() {
	    return "home";
	  }
	
}
