package com.todo.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import com.todo.service.TodoService;

@Controller
public class TodoController  
{
	private static final TodoService _todoService = new TodoService();
	
	@RequestMapping(value="/todo", method = RequestMethod.GET)
	  public @ResponseBody String getTodo(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="contactKey", required = false) String contactKey ) {
		
		if(contactKey != null)
			return _todoService.fetchAllTodosByContactKey(contactKey);
		return "";
		
	}
	
	@RequestMapping(value="/todo", method = RequestMethod.POST)
	  public @ResponseBody String saveTodo(HttpServletRequest request, HttpServletResponse response, @RequestBody String todo,@RequestParam(value="contactKey", required = false) String contactKey ) {
		Map<String, Object> jsonJavaRootObject = new Gson().fromJson(todo, Map.class);
		return _todoService.saveTodo(jsonJavaRootObject, contactKey);
		
	}
	
	@RequestMapping(value="/todo", method = RequestMethod.PUT)
	  public @ResponseBody String updateTodo(HttpServletRequest request, HttpServletResponse response, @RequestBody String todo ) {
		
		Map<String, Object> jsonJavaRootObject = new Gson().fromJson(todo, Map.class);
		
		return _todoService.updateTodo(jsonJavaRootObject);
		
	}
}
