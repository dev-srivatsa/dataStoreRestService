package dataStoreService;


import java.util.List;
import java.util.Map;

import localFile.localFile;


public interface dataStoreService {
	  
	//Create Data Store
	public void createDataStore(String dataStoreName);
	
	//List all DataStores
	public Map listDataStores();
	
	//Upload a file to DataStore
	public void uploadFileToDataStore(String dataStoreName,localFile localFileObject);
	
	//Download a file from DataStore
	public void downloadFileFromDataStore(String dataStoreName,String localFileUniqueId);
	
	//List all objects in DataStore
	public Map<String,Long> listObjectsOfDataStore(String dataStoreName);
	
	//Delete a file in DataStore
	public void deleteFileFromDataStore(String dataStoreName,String localFileUniqueId);
	
	//Delete DataStore
	public void deleteDataStore(String dataStoreName);
	
	//Validate Checksum
	public void validateCheckSum(String checkSumBefore,String checkSumAfter);

}
