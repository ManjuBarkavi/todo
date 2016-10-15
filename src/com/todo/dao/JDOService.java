package com.todo.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.todo.jdo.TodoListJDO;


public class JDOService {

	public PersistenceManager getDefaultPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getEntitiesByQuery(Class<T> clazz,String queryString, String orderBy)
	{
		PersistenceManager pm = null;
		List<T> entities= null;
		try {
			
			pm = this.getDefaultPersistenceManager();
			
			Query query = pm.newQuery(clazz, queryString);
			
			if(orderBy != null)
				query.setOrdering(orderBy);
			
			entities= (List<T>)query.execute();
			
			if (entities.isEmpty())
				return null;
			
			return entities;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closePM(pm);
		}
	}
	
	
	public <T> T persist(T entity) throws Exception {
		if (entity == null)
			return null;

		PersistenceManager pm = null;
		try {

			pm = getDefaultPersistenceManager();
			System.out.println(entity);
			T entityJDO = pm.makePersistent(entity);
			
			return entityJDO;
		} finally {
			closePM(pm);
		}
	}
	
	
	public void closePM(PersistenceManager pm) {
		if (pm != null)
			pm.close();
	}
}
