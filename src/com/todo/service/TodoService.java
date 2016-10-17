package com.todo.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.gson.Gson;
import com.todo.dao.PMF;
import com.todo.jdo.TodoListJDO;


public class TodoService {//extends JDOService{

	private static final Gson gson = new Gson();
	
	public String fetchAllTodosByContactKey(String contactKey)
	{
		
		List<TodoListJDO> todoList= new ArrayList<TodoListJDO>();
		PersistenceManager pm = null;
		try {
			
			pm = PMF.get().getPersistenceManager();
			
			//Query q = pm.newQuery(TodoListJDO.class, "status == active && contactKey =='"+contactKey+"'" );
			
			Query q = pm.newQuery(TodoListJDO.class);
			q.setFilter("status == active");
			q.setFilter("contactKey == '"+contactKey+"'");
			//q.setOrdering("dateAdded DESC");
			
			System.out.println( q.toString());
			
			
			todoList = (List<TodoListJDO>) q.execute();
			
			//todoList = getEntitiesByQuery(TodoListJDO.class, "status == active && contactKey == '"+contactKey+"'", "dateAdded DESC");
			System.out.println(todoList.size());
			return gson.toJson(todoList);
			
		} catch(Exception e) {
		
			e.printStackTrace();
			return null;
		} finally {
			if(pm != null)
				pm.close();
		}
	}
	
	
	public String saveTodo(TodoListJDO todo, String contactKey)
	{
		//TodoListJDO newTodo = new TodoListJDO();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		try {
			
			/*Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			
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
			newTodo.setIsDone((Boolean) todo.get("isDone"));*/
			
			todo.setContactKey(contactKey);
			Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			todo.setDateAdded(now.getTimeInMillis());
			
			
			pm.makePersistent(todo);

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pm != null)
			pm.close();
		}

		return gson.toJson(todo);
		
	}
	
	public String updateTodo(TodoListJDO todo)
	{
		
		
		PersistenceManager pm = null;
		pm = PMF.get().getPersistenceManager();
		
		try {
			
			//newTodo = pm.getObjectById(TodoListJDO.class, todo.getId());
			
			
			/*Double orderValue = new Double(todo.get("order").toString());
			int integerValue = orderValue.intValue();
			
			Double typeValue = new Double(todo.get("type").toString());
			int intValue = typeValue.intValue();
			
			Double scoreValues = new Double(todo.get("score").toString());
			int scoreIntValue = scoreValues.intValue();
			
			Integer order = new Integer(integerValue);
			Integer type = new Integer(intValue);
			Integer scoreValue = new Integer(scoreIntValue);
			String title = (String)todo.get("title");
			
			System.out.println(title);
			newTodo.setTitle( title);
			newTodo.setOrder(order);
			newTodo.setType(type);
			newTodo.setScore(scoreValue);
			newTodo.setIsDone((Boolean) todo.get("isDone"));*/
			
			if( todo.getIsDone())
			{	
				Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				todo.setDateCompleted(now.getTimeInMillis());
			}
			
			pm.makePersistent(todo);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(pm != null)
			pm.close();
		}

	
	return gson.toJson(todo);
		
	}
	
	
	public String deleteTodo(TodoListJDO todo)
	{
		//TodoListJDO newTodo = null;
		PersistenceManager pm = null;
		pm = PMF.get().getPersistenceManager();
		
		try {
			
			//newTodo = pm.getObjectById(TodoListJDO.class, todo.getId());
			todo.setStatus("inactive");
			pm.makePersistent(todo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(pm != null)
			pm.close();
		}
		return gson.toJson(todo);
		
	}
	
	
}
