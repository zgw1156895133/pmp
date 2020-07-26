package toolkit;

import java.sql.*;

public class DBTool {

    /**
     * 获取数据库连接
     * @param driver    数据库驱动
     * @param url       数据库url
     * @param user      用户
     * @param password  密码
     * @return 数据库连接
     */
    public static Connection getConnection(String driver,String url,String user,String password){
        Connection conn=null;
        /*String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@10.1.3.62:1521:oradb";
        String user = "bea_east";
        String password = "bea_east";*/
        try {
            Class.forName(driver);
            System.out.println("load driver success");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("load driver fail");
        }
        try {
            conn = DriverManager.getConnection(url,user,password);
            System.out.println("get connect success");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("get connect fail");
        }
        return conn;
    }

 public static void main (String args[]){
         String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@10.1.3.62:1521:oradb";
        String user = "bea_east";
        String password = "bea_east";
     getConnection(driver,url,user,password);
 }
}
