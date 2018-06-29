import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GetLocalMac 
{
	//获取IP地址
    public static String getIpAddress() 
    {
        String IP = "";
        InetAddress ia = null;
        try 
        {
            ia = InetAddress.getLocalHost();
        } 
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        IP = ia.getHostAddress().trim();

        return IP;
    }
    
    //获取mac地址
	public static String getMacAddress(String host)  
    {  
        String mac = "";  
        StringBuffer sb = new StringBuffer();  
          
        try   
        {  
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getByName(host));  
              
            byte[] macs = ni.getHardwareAddress();  
              
            for(int i=0; i<macs.length; i++)  
            {  
                mac = Integer.toHexString(macs[i] & 0xFF);   
                  
                 if (mac.length() == 1)   
                 {   
                     mac = '0' + mac;   
                 }   
  
                sb.append(mac + "-");  
            }  
                          
        }
        catch (SocketException e)
        {  
            e.printStackTrace();  
        } 
        catch (UnknownHostException e)
        {  
            e.printStackTrace();  
        }  
          
        mac = sb.toString();  
        mac = mac.substring(0, mac.length()-1);  
          
        return mac;  
    }  
	
	public static void main(String[] args) 
	{
		GetLocalMac mac = new GetLocalMac();
		System.out.println(getIpAddress()+"\n");
		System.out.println(mac.getMacAddress(getIpAddress()));
	}
}
