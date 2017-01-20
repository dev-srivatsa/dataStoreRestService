# dataStoreRestService
DataStore Rest Service provides rest endpoints to perform AWS services

Steps to be followed:
1. Import project to IDE
2. Copy paste your AWS credentials(Access key and Secret) in 'credentialsFile'
3. Change String 'credentialsFilePath' to the path of 'credentialsFile'
4. Run the servlet and start using the rest endpoints to make use of AWS services

Rest endpoints

1. hello 
	Parameter: 'Text input'
	Description: Echoes the text input which is sent to this
2. listObjects:
	Parameters: 'BucketName'
	Description: Lists all the objects in the S3 bucket	
3. deleteObject:
	Parameter: 'Object Key'
	Description: Deletes object from bucket
4. deleteDataStore:
	Parameter: 'DataStore Name'
	Description: Deletes bucket from AWS
5. listObjects:
	Parameters: '-'
	Description: Lists all the buckets in S3
6. fileUpload
	Description: Uploads file to bucket
