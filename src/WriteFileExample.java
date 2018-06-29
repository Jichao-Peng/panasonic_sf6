import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
 
public class WriteFileExample 
{
	public static void write(String s,String s2) 
	 {
	 
	  FileOutputStream fop = null;
	  File file;
		  try 
		  { 
			   file = new File(s2);
			   fop = new FileOutputStream(file);
			 
			   if (!file.exists()) 
			   {
				   file.createNewFile();
			   }
			 
			   byte[] contentInBytes = s.replaceAll("\n", "\r\n").getBytes();
			 
			   fop.write(contentInBytes);
			   fop.flush();
			   fop.close();
			 
			   System.out.println("Done");		 
		  } 
		  catch (IOException e) 
		  {
			  e.printStackTrace();
		  } 
		  finally 
		  {
			   try 
			   {
				   if (fop != null)
				    {
				    	fop.close();
				    }
			   } 
			   catch (IOException e) 
			   {
				   e.printStackTrace();
			   }
		  }
	 }
}