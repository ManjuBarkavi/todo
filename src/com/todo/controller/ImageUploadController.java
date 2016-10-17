package com.todo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.todo.service.GCSOperations;
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
	
	@RequestMapping("/uploadLanguage")
	public @ResponseBody String getLanguageUploader(HttpServletRequest request, HttpServletResponse response,@RequestBody String data) throws IOException, GeneralSecurityException
	{
	request.setCharacterEncoding("UTF-8");
	
	GCSOperations gcs = new GCSOperations();
	String mimeType	=	"";
	HashMap<String, Object> filedetails = getByteForUpload(data.getBytes(), request, response);

	System.out.println("-------- File Details -----------");

	byte[] filebyte = (byte[]) filedetails.get("filecontent");
	String fileName	=	(String) filedetails.get("filename");
	String bucketName	=	"todo-v2-live.appspot.com";
	String sourcePath	=	"images";
	String objectName	= sourcePath+fileName;

	String fileURL = gcs.uploadFile(filebyte, bucketName,objectName , "image/png", "public-read");

	System.out.println("---- File URL ----"+ fileURL);

	return fileURL;
	}
	
	
	public static  HashMap<String, Object> getByteForUpload(byte[] data,HttpServletRequest req, HttpServletResponse res)
	{
		byte[] bufferBytesRead = null;
		long maxMemSize =5*1024*1024;
		String contentType="image/png";

		ServletFileUpload upload = new ServletFileUpload();
		upload.setSizeMax(maxMemSize);
		 HashMap<String, Object> filedetails=new HashMap<String, Object>();
		 filedetails.put("response", false);
		//FileItemIterator iterator;
		try {
			/*iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream in=item.openStream();
				if (item.isFormField())  {
					String name= item.getFieldName();
					String value= Streams.asString(in);

					filedetails.put(name,value);

		        	}else{*/
		        		//contentType=item.getContentType();
		        		 bufferBytesRead=data;
		        		  filedetails.put("filename","randomimage");
		        		  filedetails.put("filecontent",bufferBytesRead);
		        		  filedetails.put("contentType", contentType);
		        		  filedetails.put("response", true);
		        //	}
				//}
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return filedetails;
	}
	
	

}
