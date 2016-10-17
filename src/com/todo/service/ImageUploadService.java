package com.todo.service;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;


public class ImageUploadService {

	public static final String ACL_PUBLIC_READ="public-read";
	public static final String GOOGLE_CLOUD_STORAGE_URL="http://commondatastorage.googleapis.com";
	private static Logger mLogger =Logger.getLogger(ImageUploadService.class.getSimpleName());
	
	public String uploadImage(String data)
	{
		String imageURL = "";
		String BUCKETNAME    =  "todo-v2-live.appspot.com";
		String filename      =  "images";
		String imagecontentype = "image/png";
		String imageUrlPath= "";
		String filepath = null;
		
		
		filepath= this.uploadUsingGcs(data.getBytes(), BUCKETNAME, filename, imagecontentype);
		if(filepath!=null)
        {
			
        }
		
		return imageURL;
	}
	
	
	public static String uploadUsingGcs(byte[] fileBytes,String bucketName,String objectName,String mimeType)
	{
    	String filePath = null;
		try
		{
			filePath=GOOGLE_CLOUD_STORAGE_URL+"/"+bucketName+"/"+objectName;
			GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder().initialRetryDelayMillis(10).retryMaxAttempts(5).totalRetryPeriodMillis(15000).build());
			GcsFilename filename = new GcsFilename(bucketName, objectName);
		    GcsFileOptions options = new GcsFileOptions.Builder().addUserMetadata("cache-control", "max-age="+(86400*365)).mimeType(mimeType).acl(ACL_PUBLIC_READ).build();
		    GcsOutputChannel writeChannel = gcsService.createOrReplace(filename,options);
		    writeChannel.write(ByteBuffer.wrap(fileBytes));
		    writeChannel.close();
		    mLogger.info("File successfully uploaded");
		}
		catch(Exception e){
			mLogger.warning("The error came becoz of "+e.getMessage());
			for(StackTraceElement s:e.getStackTrace()){
				mLogger.warning(s.toString());
			}
		}

		return filePath;
	}
}
