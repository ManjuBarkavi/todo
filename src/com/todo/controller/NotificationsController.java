package com.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.todo.jdo.TodoListJDO;
import com.todo.service.NotificationsService;

@Controller
public class NotificationsController {

	@RequestMapping(value="/sendFeed" , method = RequestMethod.POST)
	public @ResponseBody String sendFeed(@RequestParam(value="contactKey", required=true) String contactKey, @RequestBody String feed)
	{
		String status = "failure";
		
		//TodoListJDO updateTodo = new Gson().fromJson( todo, new TypeToken<TodoListJDO>(){}.getType() );
		System.out.println(feed);
		NotificationsService service = new NotificationsService();
		if(service.sendFeed(contactKey, feed))
		{
			status = "success";
		}
			
		return status;
	}
	
}
