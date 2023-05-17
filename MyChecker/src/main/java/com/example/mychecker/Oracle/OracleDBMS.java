package com.example.mychecker.Oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class OracleDBMS {
    /**
     * 配置数据库获取连接
     *
     * @return connection
     */
    public static Connection getConnection() {
        Connection connection;
        //数据库配置信息与jdbc连接代码
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //通过驱动管理器获取数据库链接对象
        //建立连接jdbc:mysql://localhost:3306/?user=root
        String url="jdbc:oracle:thin:@//127.0.0.1:1521/orcl";
        //jdbc:mysql //
        try {
            connection = DriverManager.getConnection(url, "hr", "123456");
//            System.out.println("数据库链接成功");
        } catch (SQLException e) {
            System.out.println("数据库链接失败");
            throw new RuntimeException(e);
        }
        return connection;
    }
}
