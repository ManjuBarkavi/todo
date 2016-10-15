package com.todo.controller;

import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.todo.dao.PMF;
import com.todo.jdo.TodoListJDO;
import com.todo.service.TodoService;

@Controller
public class TodoController  
{
	private static final TodoService _todoService = new TodoService();
	
	@RequestMapping(value="/todo", method = RequestMethod.GET)
	  public @ResponseBody String getTodo(HttpServletRequest request, HttpServletResponse response ) {
		return _todoService.fetchAllTodosByContactKey("asdf");
		
	}
	
	@RequestMapping(value="/todo", method = RequestMethod.POST)
	  public @ResponseBody String saveTodo(HttpServletRequest request, HttpServletResponse response, @RequestBody String todo ) {
		Map<String, Object> jsonJavaRootObject = new Gson().fromJson(todo, Map.class);
		return _todoService.saveTodo(jsonJavaRootObject);
		
	}
}
