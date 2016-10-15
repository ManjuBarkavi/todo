package com.todo.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.todo.dao.JDOService;
import com.todo.jdo.TodoJDO;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TodoService extends JDOService{

	private static final Gson gson = new Gson();
	
	public String fetchAllTodosByContactKey(String contactKey)
	{
		
		List<TodoJDO> todoList= new ArrayList<TodoJDO>();
		try {
			
			//getEntitiesByQuery(TodoJDO.class, "contactKey == '"+contactKey+"'", "dateAdded DESC");
			//return gson.toJson(todoList);
			return "{title : asdf, isDone: false, order : 1}";
		} catch(Exception e) {
		
			e.printStackTrace();
			return null;
		}
	}
	
	
	public String saveTodo(String todo)
	{
		TodoJDO newTodo = new TodoJDO();
		
		try {
			
			
			newTodo = gson.fromJson( todo, new TypeToken<TodoJDO>(){}.getType() );
			
			Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			
			newTodo.setDateAdded(now.getTimeInMillis());
			
			
			System.out.println( newTodo.getTitle() );
			System.out.println( newTodo.getDateAdded() );
			System.out.println( newTodo.getIsDone() );
			System.out.println( newTodo.getOrder() );
			
			
			System.out.println(gson.toJson(newTodo));
			persist(todo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return gson.toJson(newTodo);
		
	}
	
	
}
