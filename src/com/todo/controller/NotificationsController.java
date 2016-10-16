package com.todo.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.todo.service.NotificationsService;

@Controller
public class NotificationsController {

	@RequestMapping(value="/sendFeed" , method = RequestMethod.POST)
	public @ResponseBody String sendFeed(@RequestParam(value="contactKey", required=true) String contactKey, 
			@RequestParam(value="accessToken", required = false) String accessToken,@RequestParam(value="feed", required = true) String feed)
	
	{
		String status = "failure";
		NotificationsService service = new NotificationsService();
		if(service.sendFeed(contactKey, accessToken, feed))
		{
			status = "success";
		}
			
		return status;
	}
	
}
