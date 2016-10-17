package com.todo.service;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gson.Gson;
import com.todo.dao.JDOService;
import com.todo.dao.PMF;
import com.todo.jdo.Contact;

public class NotificationsService extends JDOService{
	
	public boolean sendFeed(String contactKey, String feed) 
	{
		
		if(contactKey == null)
			return false;
		
		Contact contact = null;
		PersistenceManager pm = null;
		try
		{
			
			
				 
				 pm =PMF.get().getPersistenceManager();
				 Query query = pm.newQuery(Contact.class,"contactKey == '"+contactKey+"'");
					List<Contact> contactlist = (List<Contact>) query.execute();
					
					if(contactlist.isEmpty())
					{
						return false;
					}
					
					contact = contactlist.get(0);
			
		
			String url = "https://api.anywhereworks.com/api/v1/feed";
		
			URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
			//URL obj = new URL(url);
			
			//HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			/*con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setConnectTimeout(3600);
			con.setReadTimeout(3600);
			con.setRequestProperty("Content-Type","application/json");*/
			
			System.out.println(feed);
			Map<String,String> feedJson	=	 new HashMap<String,String>();
			feedJson.put("content", feed);
			feedJson.put("type", "update");
			
			String feedsString = new Gson().toJson(feedJson);
			
			System.out.println("feedStr="+feedsString);
			System.out.println("token="+contact.getAccessToken());
			
			HTTPRequest request = new HTTPRequest(new URL(url), HTTPMethod.POST, FetchOptions.Builder.withDeadline(300));
			request.setPayload(feedsString.getBytes());
			request.setHeader(new HTTPHeader("Content-type","application/json"));
			request.setHeader(new HTTPHeader("Authorization","Bearer "+contact.getAccessToken()));

			HTTPResponse response=fetcher.fetch(request);
			
			//responseJsonString =new String(response.getContent());
			
			
			
			/*con.setPayload(feedsString.getBytes());
			
			con.setRequestProperty("Authorization", "Bearer "+contact.getAccessToken());
			
			String urlParameters = "type=update&content="+feed;
			byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
			
			DataOutputStream dos = new DataOutputStream( con.getOutputStream());
			dos.write( postData );
			dos.flush();*/
			
			
			//OutputStreamWriter writers = new OutputStreamWriter(con.getOutputStream());
			//writers.write(urlParameters);
			//writers.flush();
			
			System.out.println(response.getResponseCode());
			if(response.getResponseCode() == 200)
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
