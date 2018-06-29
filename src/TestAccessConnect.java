import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;  
   
public class TestAccessConnect extends BaseDao
{  
    Connection con = null;  
    Statement s = null;  
    ResultSet rs = null;  
    String updateType="";
    String updateTypeValue="";
 
     //执行查询 
    public void seach()
    {  
        try
        {  
            con = getConnection();  
            s = con.createStatement();// 创建SQL语句对象  
            rs=s.executeQuery("select * from employee");// 查询员工信息  
            while (rs.next()) 
            {  
                System.out.println("编号："+rs.getInt("ID")+"，姓名："+rs.getString(2)+"，年龄："+rs.getInt("age")+"，入职日期："+rs.getDate("entryDate"));  
            }  
            System.out.println("__________执行完毕___________ ");  
        }
        catch (Exception e)
        {  
            e.printStackTrace();  
        } 
        finally 
        {  
            CloseConnection(con, rs, s);  
        }  
    }  
       
    //执行增加 
    public void add()
    {  
        try 
        {  
            int result=0;  
            con = getConnection();  
            s = con.createStatement();// 创建SQL语句对象  
            result=s.executeUpdate("insert into employee(id,membername,age,entryDate) values('167','芳芳','19','2012-11-2')");  
            if (result>0) 
            {  
                System.out.println("插入成功");   
            }  
        } 
        catch (Exception e)
        {  
            e.printStackTrace();  
        }
        finally 
        {  
            CloseConnection(con, rs, s);  
        }  
        seach();  
    }  
    
    
    public void addDate()
    {  
        try
        {  
            int result=0;  
            con=getConnection();  
            s = con.createStatement();// 创建SQL语句对象 
            Date date=new Date();
            DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time=format.format(date);
            System.out.println(time);
            result=s.executeUpdate("insert into SF6(时间和日期) values('"+time+"')");   
            if (result>0) 
            {  
                System.out.println("插入成功");   
            }  
        } 
        catch (Exception e) 
        {  
            e.printStackTrace();  
        } 
        finally 
        {  
            CloseConnection(con, rs, s);  
        }  
    }  

     //执行修改  
    public void updata()
    {  
        try 
        {  
            int result=0;  
            con=getConnection();  
            s = con.createStatement();// 创建SQL语句对象  
            result=s.executeUpdate("update employee set age=20 where id=6");  
            if (result>0) 
            {  
                System.out.println("更新成功");  
            }  
        } 
        catch (Exception e)
        {  
            e.printStackTrace();  
        } 
        finally 
        {  
            CloseConnection(con, rs, s);  
        }  
        seach();  
    }  
       
    
    public void updataCMS()
    {  
        try
        {  
            int result=0;  
            con=getConnection();  
            s = con.createStatement();// 创建SQL语句对象  
            result=s.executeUpdate("update SF6 set 序列号='NG' where 序列号='OK'");  
            if (result>0) 
            {  
                System.out.println("更新成功");  
            }  
        } 
        catch (Exception e)
        {  
            e.printStackTrace();  
        }
        finally 
        {  
            CloseConnection(con, rs, s);  
        }  
    }  
    
    
    public void updataLastLine()
    {  
        try 
        {  
            int result=0;  
            con=getConnection();  
            s = con.createStatement();// 创建SQL语句对象  
            result=s.executeUpdate("update SF6 set "+updateType+"='"+updateTypeValue+"' where id=(select top 1 ID from SF6 order by ID desc)");  
            if (result>0) 
            {  
                System.out.println("更新成功");  
            }  
        } 
        catch (Exception e) 
        {  
            e.printStackTrace();  
        } 
        finally 
        {  
            CloseConnection(con, rs, s);  
        }  
    }  
     
    //执行删除    
    public void delete()
    {  
        try 
        {  
            int result=0;  
            con=getConnection();  
            s = con.createStatement();// 创建SQL语句对象  
            result=s.executeUpdate("delete from SF6 where id=3");  
            if (result>0)
            {  
                System.out.println("删除成功");  
            }  
        }
        catch (Exception e)
        {  
            e.printStackTrace();  
        } 
        finally 
        {  
            CloseConnection(con, rs, s);  
        }  
        seach();  
    }  
    
    
    public void deleteAll()
    {  
        try
        {  
            int result=0;  
            con=getConnection();  
            s = con.createStatement();// 创建SQL语句对象  
            result=s.executeUpdate("delete from SF6 where 1=1 ");  
            if (result>0)
            {  
                System.out.println("删除成功");  
            }  
        } 
        catch (Exception e)
        {  
            e.printStackTrace();  
        } 
        finally 
        {  
            CloseConnection(con, rs, s);  
        }   
    }  
    public static void main(String[] args)
    {  
        TestAccessConnect t=new TestAccessConnect();    
        t.addDate();
    }  
}  