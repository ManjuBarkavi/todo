package com.todo.service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.todo.dao.JDOService;
import com.todo.dao.PMF;
import com.todo.jdo.ContactJDO;

public class NotificationsService extends JDOService{
	
	public boolean sendFeed(String contactKey, String accessToken, String feed) 
	{
		
		if(contactKey == null)
			return false;
		
		ContactJDO contact = null;
		PersistenceManager pm = null;
		try
		{
			
			if(accessToken == null)
			{
				 //contact= getContactByKey(contactKey);
				  pm =PMF.get().getPersistenceManager();
				 Query query = pm.newQuery(ContactJDO.class,"awContactKey == '"+contactKey+"'");
					List<ContactJDO> contactlist = (List<ContactJDO>) query.execute();
					
					if(contactlist.isEmpty())
					{
						return false;
					}
					
					contact = contactlist.get(0);
			}
		
			String url = "https://api.anywhereworks.com/api/v1/feed";
		
		
			URL obj = new URL(url);
			
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setConnectTimeout(3600);
			con.setReadTimeout(3600);
			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJmdWxsY3JlYXRpdmUuZnVsbGF1dGguY29tIiwiaWF0IjoxNDc2NTU0OTA1LCJ1c2VyX2lkIjoiYTY5NmQ2OWEtZWUwOS00MmQyLTkwNTktYmYxYzE1OTE5NDg2IiwiZXhwIjoxNDc2NTYyMTA1LCJqdGkiOiIyNjA4OC5nMGhBWVlnSWRhIn0.ju6QM5gvKag8Cv9SV7vlxgT0jW2Jzs7BHQ2CEbzmvl0");
			
			String urlParameters = "content=this%20is%20test%20feed&type=activity";
			
			OutputStreamWriter writers = new OutputStreamWriter(con.getOutputStream());
			writers.write(urlParameters);
			writers.flush();
			
			System.out.println(con.getResponseCode());
			if(con.getResponseCode() == 200)
				return true;
			else
				return false;
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pm!=null)
			pm.close();
		}
		
		return false;
		
	}
	

}
