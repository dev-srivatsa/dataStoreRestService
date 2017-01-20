package credentials;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

public class awsCredentials extends credentials {
	public awsCredentials() throws IOException {
		super();
		
	}

	private BasicAWSCredentials credentials;
	
	//Get AWS Region
	public Region getAWSRegion()
	{
		Region region = Region.getRegion(Regions.US_WEST_2);
		return region;
	}
	
	//Get AWS Credentials
	public BasicAWSCredentials getAWSCredentias()
	{
        try {
        	credentials = new BasicAWSCredentials(accessKey, accessSecret);
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials.",
                    e);
        }
        return credentials;
		
	}
	
	

}
