import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;  

public class BaseDao { 	
    Connection con = null;  
    Statement s = null;  
    ResultSet rs =null;  
       
    // 得到联接  
    public Connection getConnection()
    {  
        try 
        {  
            // 加载JDBC-ODBC桥驱动程序  
        	Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");  
        	
        	//设置字符集,access内部是GB2312
        	Properties pro = new Properties();
            pro.setProperty("charSet","GB2312");
               
            //自定义路径
        	con=DriverManager.getConnection("jdbc:odbc:driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=."+File.separator+Frame.dataBaseUsed.getText().trim(),pro);
        }
        catch (Exception e)
        {  
            e.printStackTrace();  
        }  
        return con;  
    }  
       
   
     // 关闭数据源  
    public void CloseConnection(Connection con,ResultSet rs,Statement s)
    {  
        try {  
            if (rs!=null) 
            {  
                rs.close();  
            }  
            if (s!=null) 
            {  
                s.close();  
            }  
            if (con!=null)
            {  
                con.close();      
            }  
        }
        catch (SQLException e)
        {  
            e.printStackTrace();  
        }  
    }  
}