package localFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class localFile {
	
	//Unique Id across DataStore  
	private String uniqueId;
	private String filePath;
	
	public localFile(String uniqueId,String file)
	{
		this.uniqueId=uniqueId;
		this.filePath=file;
	}
	
	public File getFile()
	{
		File file=new File(filePath);
        return file;
	}
	
	public String getUniqueId()
	{
		return uniqueId;
	}
	public String getFilePath()
	{
		return filePath;
	}
	
	//Compute checksum of a file, given its path
	public String computeChecksum() throws FileNotFoundException, NoSuchAlgorithmException
    {
    	

        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(filePath);
        byte[] dataBytes = new byte[1024];

        int nread = 0;

        try {
			while ((nread = fis.read(dataBytes)) != -1) {
			  md.update(dataBytes, 0, nread);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};

        byte[] mdbytes = md.digest();

        //Convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
        	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        System.out.println("md5CheckSumBeforeUpload " + sb.toString());
        
        return sb.toString();
    }
	
}
