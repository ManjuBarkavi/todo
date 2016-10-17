package com.todo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;




import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
//import com.setmore.utils.Utils;

public class GCSOperations {

private static final int BUFFER_SIZE = 1024 * 1024;


private static final String GCS_URL	=	"http://commondatastorage.googleapis.com";

private GcsService _gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
.initialRetryDelayMillis(10)
.retryMaxAttempts(5)
.totalRetryPeriodMillis(15000)
.build());

public String uploadFile(byte[] fileBytes, String bucketName, String objectName, String mimeType, String acl) 
{
//if( fileBytes == null || fileBytes.length == 0 || Utils.isBlank(bucketName) || Utils.isBlank(objectName) || Utils.isBlank(mimeType) || Utils.isBlank(acl) ) {
//throw new IllegalArgumentException("Invalid Parameters"); 

		try
		{
			GcsFileOptions gcsFileOptions = new GcsFileOptions.Builder()
			.addUserMetadata("cache-control", "max-age="+(86400*365))
			.mimeType(mimeType)
			.acl(acl)
			.build();

GcsFilename fileName = new GcsFilename(bucketName, objectName);

GcsOutputChannel writeChannel = _gcsService.createOrReplace(fileName, gcsFileOptions);

System.out.println(writeChannel);

writeChannel.write(ByteBuffer.wrap(fileBytes));
writeChannel.close();

}
catch(IOException e)
{

e.printStackTrace();
}

String filePath	=	GCS_URL+"/"+bucketName+"/"+objectName;

return filePath;
}

public String retrieveFile(String bucketName, String objectName) 
{
StringBuffer response = new StringBuffer();
try
{
GcsFilename fileName = new GcsFilename(bucketName, objectName);

GcsInputChannel readChannel = _gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);

InputStream	inputStream	=	Channels.newInputStream(readChannel);

BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

String inputLine;


while ( ( inputLine = in.readLine() ) != null )
{
response.append( inputLine );
}
}
catch(IOException e)
{

e.printStackTrace();
return response.toString();
}
return response.toString();
}



}
