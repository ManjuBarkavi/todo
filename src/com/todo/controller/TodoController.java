package com.todo.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.todo.jdo.TodoListJDO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
		
		
		
		TodoListJDO newTodo = new Gson().fromJson( todo, new TypeToken<TodoListJDO>(){}.getType() );
		return _todoService.saveTodo(newTodo, contactKey);
		
	}
	
	@RequestMapping(value="/todo", method = RequestMethod.PUT)
	  public @ResponseBody String updateTodo(HttpServletRequest request, HttpServletResponse response, @RequestBody String todo ) {
		
		TodoListJDO updateTodo = new Gson().fromJson( todo, new TypeToken<TodoListJDO>(){}.getType() );
		return _todoService.updateTodo(updateTodo);
		
	}
	
	@RequestMapping(value="/todo", method = RequestMethod.DELETE)
	  public @ResponseBody String deleteTodo(HttpServletRequest request, HttpServletResponse response, @RequestBody String todo ) {
		
		TodoListJDO deleteTodo = new Gson().fromJson( todo, new TypeToken<TodoListJDO>(){}.getType() );
		return _todoService.deleteTodo(deleteTodo);
		
	}
}
