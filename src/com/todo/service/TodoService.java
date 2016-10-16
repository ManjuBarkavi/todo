package com.todo.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;

import com.todo.dao.JDOService;
import com.todo.dao.PMF;
import com.todo.jdo.TodoListJDO;

import com.google.gson.Gson;


public class TodoService extends JDOService{

	private static final Gson gson = new Gson();
	
	public String fetchAllTodosByContactKey(String contactKey)
	{
		
		List<TodoListJDO> todoList= new ArrayList<TodoListJDO>();
		try {
			
			todoList = getEntitiesByQuery(TodoListJDO.class, "contactKey == '"+contactKey+"'", "dateAdded DESC");
			return gson.toJson(todoList);
			
		} catch(Exception e) {
		
			e.printStackTrace();
			return null;
		}
	}
	
	
	public String saveTodo(Map<String, Object> todo, String contactKey)
	{
		TodoListJDO newTodo = new TodoListJDO();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		try {
			
			Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			
			Double orderValue = new Double(todo.get("order").toString());
			int integerValue = orderValue.intValue();
			
			Double typeValue = new Double(todo.get("type").toString());
			int intValue = typeValue.intValue();
			
			Double scoreValues = new Double(todo.get("score").toString());
			int scoreIntValue = scoreValues.intValue();
			
			Integer order = new Integer(integerValue);
			Integer type = new Integer(intValue);
			Integer scoreValue = new Integer(scoreIntValue);
			String title = (String)todo.get("title");
			
			newTodo.setTitle( title);
			newTodo.setContactKey( contactKey);
			newTodo.setOrder(order);
			newTodo.setType(type);
			newTodo.setDateAdded(now.getTimeInMillis());
			newTodo.setScore(scoreValue);
			newTodo.setIsDone((Boolean) todo.get("isDone"));
			pm.makePersistent(newTodo);

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}

		
		return gson.toJson(newTodo);
		
	}
	
	public String updateTodo(Map<String, Object> todo)
	{
		
		TodoListJDO newTodo = null;
		PersistenceManager pm = null;
		pm = PMF.get().getPersistenceManager();
		
		try {
			newTodo = pm.getObjectById(TodoListJDO.class, todo.get("key"));
			
			
			
			Double orderValue = new Double(todo.get("order").toString());
			int integerValue = orderValue.intValue();
			
			Double typeValue = new Double(todo.get("type").toString());
			int intValue = typeValue.intValue();
			
			Double scoreValues = new Double(todo.get("score").toString());
			int scoreIntValue = scoreValues.intValue();
			
			Integer order = new Integer(integerValue);
			Integer type = new Integer(intValue);
			Integer scoreValue = new Integer(scoreIntValue);
			String title = (String)todo.get("title");
			
			
			newTodo.setTitle( title);
			newTodo.setOrder(order);
			newTodo.setType(type);
			newTodo.setScore(scoreValue);
			newTodo.setIsDone((Boolean) todo.get("isDone"));
			
			if((Boolean) todo.get("isDone") == true)
			{	
				Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				newTodo.setDateCompleted(now.getTimeInMillis());
			}
			pm.makePersistent(newTodo);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			pm.close();
		}

	
	return gson.toJson(newTodo);
		
	}
	
	
}
