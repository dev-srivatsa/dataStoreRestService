package credentials;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class credentials {
	protected String accessKey;
	protected String accessSecret;
	
	@SuppressWarnings("resource")
	public credentials() throws IOException
	{
		String currentDirectory = new File("").getAbsolutePath();
		
		String credentialsFilePath = "/home/srivms/workspace/dataStoreRestService/credentialsFile";
		BufferedReader credentialsFile = new BufferedReader(new FileReader(credentialsFilePath));
		String keyLine=credentialsFile.readLine();
		String key=keyLine.split("=")[1];
		String secretLine=credentialsFile.readLine();
		String secret=secretLine.split("=")[1];
		
		accessKey=key;
		accessSecret=secret;
		
	}
	public String getAccessKey()
	{
		return accessKey;
	}
	public String getAccessSecret()
	{
		return accessSecret;
		
	}
	
	

}
