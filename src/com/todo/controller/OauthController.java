package com.todo.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.todo.dao.JDOService;
import com.todo.jdo.ContactJDO;


@Controller
public class OauthController {
	
	private static final String client_id = "29354-03ca85f7a4dd032e86c3c0842f67a418";
	private static final String client_secret = "3HXKooDdyf_8ucI22tM7uFlz3wN0xxd5wzlH6l07";
	private static final String redirect_uri = "http://todo-scrum-live.appspot.com/oauth/callback";
	
	
	@RequestMapping(value="/oauth/callback" , method = RequestMethod.GET)
	public @ResponseBody String getAccessToken(@RequestParam("code") String authcode,@RequestParam("state") String awContactKey,HttpServletRequest req, HttpServletResponse res)
	{
		System.out.println("authcode "+authcode);

		this.getAndSaveToken(authcode, awContactKey);
		
		return "success";

	}
	
	/*@RequestMapping(value="/getCode" , method = RequestMethod.GET)
	public @ResponseBody String fetchAuthCode(@RequestParam("awContactKey") String awContactKey)
	{
		String url = "https://access.anywhereworks.com/o/oauth2/auth?response_type=code&client_id=29354-03ca85f7a4dd032e86c3c0842f67a418&scope=awapis.notifications.write%20awapis.chat.streams.push%20awapis.streams.read%20awapis.users.read%20awapis.feeds.write%20awapis.identity%20awapis.account.read%20&redirect_uri=http://todo-scrum-live.appspot.com/oauth/callback&approval_prompt=force&access_type=offline&state="+awContactKey;
		
		URL obj;
		try {
			
			obj = new URL(url);
		
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("GET");
			con.setConnectTimeout(3600);
			con.setReadTimeout(3600);
			
			OutputStreamWriter writers = new OutputStreamWriter(con.getOutputStream());
			writers.flush();
		
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		return "success";
		
	}*/
	
	public String getAndSaveToken(String code, String awContactKey) {

		String scope =	null;
		//boolean status =	false;
		String accessToken =null;

		StringBuffer responseJson = new StringBuffer();
		System.out.println("in BasenProcessOauth");
		try
		{


		if(code != null && awContactKey != null)
		{
			String url = "https://access.anywhereworks.com/o/oauth2/v1/token";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setConnectTimeout(3600);
			con.setReadTimeout(3600);
			con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			

			String urlParameters = "code="+code+"&client_id="+client_id+"&client_secret="+client_secret+"&redirect_uri="+redirect_uri+"&grant_type=authorization_code";


			OutputStreamWriter writers = new OutputStreamWriter(con.getOutputStream());
			writers.write(urlParameters);
			writers.flush();
	
			int responseCode = con.getResponseCode();
			System.out.println("Response Code : " + responseCode);
	
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
	
			String responseString = "";
				while ((responseString = reader.readLine()) != null) 
				{
					System.out.println("The response string "+responseString);
			
					responseJson.append(responseString);
				
					JSONObject responseObject = new JSONObject(responseString);
					System.out.println("Access Token "+responseObject.getString("access_token"));
					System.out.println("Refresh Token"+responseObject.getString("refresh_token"));
			
					accessToken= responseObject.getString("access_token");
		
					this.saveAccessToken(awContactKey, accessToken, responseObject.getString("refresh_token"));
					
		
				}
		} 
			
			//String getCode = "https://access.anywhereworks.com/o/oauth2/auth?response_type=code&client_id=29354-03ca85f7a4dd032e86c3c0842f67a418&scope=awapis.notifications.write%20awapis.chat.streams.push%20awapis.streams.read%20awapis.users.read%20awapis.feeds.write%20awapis.identity%20awapis.account.read%20&redirect_uri=http://todo-scrum-live.appspot.com/oauth/callback&approval_prompt=force&access_type=offline&state=a696d69a-ee09-42d2-9059-bf1c15919486";
		
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		

		return accessToken;
	}
	
	
	public boolean saveAccessToken(String awContactKey, String accessToken, String refreshToken)
	{
		//boolean status = false;
		
		JDOService jdoService = new JDOService();
		ContactJDO contact = new ContactJDO();
		try {
			
		contact.setAwContactKey(awContactKey);
		contact.setAccessToken(accessToken);
		contact.setRefreshToken(refreshToken);
		
		
			jdoService.persist(contact);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return true;
	}
		
	
}
