package com.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.todo.service.ImageUploadService;

@Controller
public class ImageUploadController {
	
	@RequestMapping("/uploadimage")
	public @ResponseBody String imageUpload(@RequestBody String data)
	{
		
		ImageUploadService uploadService = new ImageUploadService();
		String imageURL = uploadService.uploadImage(data);
		
		System.out.println(imageURL);
		return "";
	}

}
