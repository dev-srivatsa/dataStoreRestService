package dataStoreService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import credentials.awsCredentials;
import localFile.localFile;

public class awsRestService implements dataStoreService{
	
	private AmazonS3 s3Service;
	private BasicAWSCredentials credentials;
	private String errorMessage;
	
	public awsRestService() throws IOException
	{
		awsCredentials awsCredentialsObject=new awsCredentials();
		credentials=awsCredentialsObject.getAWSCredentias();
		s3Service = new AmazonS3Client(credentials);
        Region region = awsCredentialsObject.getAWSRegion();
        s3Service.setRegion(region);
		
	}
	
	//Creates S3 Bucket
	public void createDataStore(String dataStoreName) {
		try {
			
	           System.out.println("Creating bucket " + dataStoreName + "\n");
	           s3Service.createBucket(dataStoreName);
				
			} catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, which means your request made it "
	                    + "to Amazon S3, but was rejected with an error response for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which means the client encountered "
	                    + "a serious internal problem while trying to communicate with S3, "
	                    + "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
		
	}

	//List S3Buckets
	public Map listDataStores() {
		
		try {
			Map dataStoresList=new HashMap();
			int i=1;
		 	System.out.println("Listing buckets");
            for (Bucket bucket : s3Service.listBuckets()) {
                System.out.println(" - " + bucket.getName());
                dataStoresList.put(i++,bucket.getName());
            }
            System.out.println(dataStoresList);
            
            return dataStoresList;
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
		return null;
		
	}

	public void uploadFileToDataStore(String dataStoreName, localFile localFileObject) {
		try {
			
			String md5ChecksumBeforeUpload = null;
			try {
				md5ChecksumBeforeUpload = localFileObject.computeChecksum();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String md5CheckSumAfterUpload=null;
			ObjectListing objectListingBeforeUpload = s3Service.listObjects(new ListObjectsRequest()
	                    .withBucketName(dataStoreName));
	        for (S3ObjectSummary objectSummary : objectListingBeforeUpload.getObjectSummaries()) {
            	if(objectSummary.getKey().equals(localFileObject.getUniqueId()))
            	{
            		System.out.println("Object with same unique id exists in bucket. Please try again with different name");
            		errorMessage="Object with same unique id exists in bucket. Please try again with different name";
            		return;
            	}
	         }
	        
            System.out.println("Uploading a object to S3 from a file\n");
            s3Service.putObject(new PutObjectRequest(dataStoreName, localFileObject.getUniqueId(), localFileObject.getFile()));
            System.out.println("Upload complete");
 
            ObjectListing objectListingAfterUpload = s3Service.listObjects(new ListObjectsRequest()
                    .withBucketName(dataStoreName));
            for (S3ObjectSummary objectSummary : objectListingAfterUpload.getObjectSummaries()) {
            	if(objectSummary.getKey().equals(localFileObject.getUniqueId()))
            	{
            		md5CheckSumAfterUpload=objectSummary.getETag();
            	}
            }
            System.out.println("md5CheckSumAfterUpload "+md5CheckSumAfterUpload);
            validateCheckSum(md5CheckSumAfterUpload,md5ChecksumBeforeUpload);
            
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            setErrorMessage(ase.getMessage());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
            setErrorMessage(ace.getMessage());
        }
		
	}

	public void downloadFileFromDataStore(String dataStoreName, String localFileUniqueId)  {
		try {
			System.out.println("Downloading an object");
            S3Object object = s3Service.getObject(new GetObjectRequest(dataStoreName, localFileUniqueId));
            System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } 
	}

	public Map<String,Long> listObjectsOfDataStore(String dataStoreName) {
		try {
			Map<String,Long> listOfObjects=new HashMap<String,Long>();
			System.out.println("Listing objects");
            ObjectListing objectListing = s3Service.listObjects(new ListObjectsRequest()
                    .withBucketName(dataStoreName)
                    .withPrefix(""));
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                System.out.println(" - " + objectSummary.getKey() + "  " +
                                   "(size = " + objectSummary.getSize() + ")");
                listOfObjects.put(objectSummary.getKey().toString(),objectSummary.getSize());
            }
            System.out.println();
            return listOfObjects;
			
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
		return null;
		
	}

	public void deleteFileFromDataStore(String dataStoreName, String localFileUniqueId) {
		try {
			System.out.println("Deleting an object\n");
            s3Service.deleteObject(dataStoreName, localFileUniqueId);
			
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            setErrorMessage(ase.getMessage());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
            setErrorMessage(ace.getMessage());
        }
		
	}

	public void deleteDataStore(String dataStoreName) {
		try {
			System.out.println("Deleting bucket " + dataStoreName + "\n");
            s3Service.deleteBucket(dataStoreName);
			
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            setErrorMessage(ase.getMessage());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
            setErrorMessage(ace.getMessage());
        }
		
	}

	public void validateCheckSum(String checkSumBefore, String checkSumAfter) {
		if(checkSumAfter.equals(checkSumBefore))
		{
			System.out.println("Successful upload");
		}
		else
		{
			System.out.println("Retry uploading! Unsuccessful upload");
		}
		
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	

}
