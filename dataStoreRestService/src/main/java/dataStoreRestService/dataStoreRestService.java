package dataStoreRestService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import dataStoreService.awsRestService;
import localFile.localFile;

@Path("/services")
public class dataStoreRestService {
	
	awsRestService s3Client;
	String bucketName="Your bucketName";
	public dataStoreRestService() throws IOException
	{
		s3Client=new awsRestService();
	}
 
	@GET
	@Path("hello/{param}")
	public Response getMsg(@PathParam("param") String msg) {
 
		String output = "Data store service say : Hi " + msg;
 
		return Response.status(200).entity(output).build();
 
	}
 
	@GET
	@Path("listObjects/{param}")
	public Response getlistOfObjects(@PathParam("param") String msg) throws IOException {
		
		Map<String,Long> listOfObjects=s3Client.listObjectsOfDataStore(msg);
		JSONObject listOfObjectsJson=new JSONObject();
		listOfObjectsJson.putAll(listOfObjects);
		return Response.status(200).entity(listOfObjectsJson.toString()).build();
	}
	
	@GET
	@Path("/listDataStores")
	public Response getDataStores() throws IOException {
		awsRestService s3Client=new awsRestService();
		Map listOfDataStores=s3Client.listDataStores();
		JSONObject listDataStoresJson=new JSONObject();
		listDataStoresJson.putAll(listOfDataStores);
		return Response.status(200).entity(listDataStoresJson.toString()).build();
	}
	
	@POST
	@Path("/fileUpload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {

		awsRestService s3Client=new awsRestService();
		
		File tempFile = File.createTempFile("tempFile", null);
	    tempFile.deleteOnExit();
	    FileOutputStream out = new FileOutputStream(tempFile);
	    IOUtils.copy(uploadedInputStream, out);
		
	    localFile localFileObject=new localFile(fileDetail.getFileName(),tempFile.getAbsolutePath());
	    String output = "Image file has been successfully uploaded to Amazon-S3 bucket";
	    
	    s3Client.uploadFileToDataStore(bucketName, localFileObject);
	   
		output=s3Client.getErrorMessage();

		return Response.status(200).entity(output).build();

	}
	
	//@DELETE
	@GET
	@Path("/deleteObject/{uniqueId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteObject(@PathParam("uniqueId") String uniqueId){
		
		String output="Object Deleted successfully from the Amazon S3 bucket";
		s3Client.deleteFileFromDataStore(bucketName, uniqueId);
		output=s3Client.getErrorMessage();
	    return Response.status(200).entity(output).build();
	   
	}
	
	@GET
	@Path("/deleteDataStore/{dataStoreName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDataStore(@PathParam("dataStoreName") String dataStoreName){
		
		String output="Bucket deleted successfully from the Amazon S3";
		s3Client.deleteDataStore(dataStoreName);
		output=s3Client.getErrorMessage();
	    return Response.status(200).entity(output).build();
	   
	}
	
	
	

}
