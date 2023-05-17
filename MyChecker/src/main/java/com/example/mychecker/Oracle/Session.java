package com.example.mychecker.Oracle;

import java.sql.*;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Session extends Thread{
    /**
     * 生成事务并注入Oracle数据库执行的线程
     *
     */
    private Connection connection;
    private int trNum;
    private int num;
    private String name;
    private String IsolationLevel;
    private int writeContent = 0;
    static private String trace = null;//记录事务的trace
    private static Lock l = new ReentrantLock();//用于加锁修改事务trace的锁
    public static String getTrace(){
        return trace;
    }
    public static void ResetTrace(){
        trace = null;
    }
    //建表及测试对象
    public void create(){
        //删表
        String sql2 = "drop table test";
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute(sql2);
        } catch (SQLException e) {
            System.out.println("删表失败！");
            throw new RuntimeException(e);
        }
        //建表
        String sql = "create table test(\n" +
                "content varchar(3000),\n" +
                "addr varchar(4) primary key\n" +
                ")";
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("建表失败！");
            throw new RuntimeException(e);
        }
        //insert，建立测试用的100个对象
        for(int i = 1; i <= 100; i++){
            sql = "insert into test values('nil', '" + i + "')";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println("初始化对象失败！");
                throw new RuntimeException(e);
            }
        }
    }

    //将数据库恢复到初始状态,所有对象内容置为nil
    public void initial(){
        //update，清空测试用的100个对象的内容
        for(int i = 1; i <= 100; i++){
            String sql = "update test set content = 'nil' " +
                    "where addr = '" + i + "';";
            Statement stmt = null;
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Oracle数据库连接并初始化参数，事务的操作数，名称与隔离级别
    public Session(int trnum, int num, String name, String IsolationLevel){
        this.trNum = trnum;
        this.num = num;
        this.name = name;
        this.connection = OracleDBMS.getConnection();
        this.IsolationLevel = IsolationLevel;
        this.writeContent = (Integer.parseInt(name.substring(1)) - 1)  * (trnum * num) +  1;
    }

    //关闭数据库资源
    public void stopConnection(){
        //关闭数据库资源
        try {
            connection.close();
//            System.out.println("关闭数据库资源成功！");
        } catch (SQLException e) {
            System.out.println("关闭数据库资源失败！");
            throw new RuntimeException(e);
        }
    }

    //设置数据库事务隔离级别
    private void SetIsolationLevel(String level){
        String sql = "set transaction isolation level " + level;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //设置自动提交
    private void SetAutoCommit(Boolean b){
        try {
            connection.setAutoCommit(b);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //开启事务
    private void Begin(){
        //开始事务
        String sql = "BEGIN;";
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //提交事务
    private void Commit(){
        //开始事务
        String sql = "COMMIT;";
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //回滚事务
    private void Rollback(){
        //开始事务
        String sql = "ROLLBACK;";
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //获取1-3的随机数
    private int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    //从随机生成地址进行读操作
    private String Read(String trace1, String trName){
        int addr = getRandomNumberInRange(1, 100);//随机生成1-100的地址
//        String addr = "1";
        String readContent = "";
        String sql = "select content from test where addr = '" + addr +"'";
        try {
            Statement stmt = connection.createStatement();
            //执行sql
            ResultSet selectresult = stmt.executeQuery(sql);
            //处理的结果
            if (trace1 != null)
                trace1 += ", r(" + addr + ", [";
            else
                trace1 = "r(" + addr + ", [";
            while (selectresult.next()) {
                readContent = selectresult.getString(1);
                trace1 += readContent;
                //System.out.println(readContent);
            }
            trace1 += "])";
            //System.out.println(trace);
            selectresult.close();
            //System.out.println(this.name + "读操作完成！");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  trace1;
    }

    //从随机生成地址进行append操作
    private String Append(String trace1, String trName){
        String addr = String.valueOf(getRandomNumberInRange(1, 100));//随机生成1-100的地址
//        String addr = "1";
        String writecontent = String.valueOf(writeContent++);
        String sql = "update test set content = concat(content, '、" + writecontent +
                "') where addr = '" + addr +"'";
        try {
            Statement stmt = connection.createStatement();
            //执行sql  count影响的行数
            int count = stmt.executeUpdate(sql);
            //处理的结果
            //System.out.println("影响行数：" + count);
            if (count > 0) {
                //System.out.println(this.name + "写操作执行成功！");
                if (trace1 != null)
                    trace1 += ", w(" + addr + ", " + writecontent + ")";
                else
                    trace1 = "w(" + addr + ", " + writecontent + ")";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return trace1;
    }

    public void run() {
        int s = Integer.parseInt(this.name.substring(1));
        //一个session内的事务串行执行
        for(int t = 1; t <= trNum; t++) {
            String trName = "T" + ((s - 1) * trNum + t);
            SetAutoCommit(false);
            SetIsolationLevel(IsolationLevel);//Serializable / REPEATABLE READ / READ COMMITTED / READ UNCOMMITTED
//            Begin();
            String trace1 = null;
            try {
                for (int i = 0; i < num; i++) {
                    int n = getRandomNumberInRange(1, 3);
                    //使用随机数来生成随机操作，1为跳过，2为写操作，3为读操作
                    if (n == 1) {
//                    System.out.println(this.name + "什么都不执行");
                    }
                    //append
                    else if (n == 2) {
                        trace1 = Append(trace1, trName);
                    }
                    //read
                    else if (n == 3) {
                        trace1 = Read(trace1, trName);
                    }
//                    try{
//                        sleep(1);
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
                }
            connection.commit(); //手动提交
//                Commit();
                l.lock();
                {//更新trace的值时需要加锁
                    if (trace1 != null) {
                        if (trace == null) {
                            trace = trName + ": " + trace1 + "\n";
                        } else {
                            trace += trName + ": " + trace1 + "\n";
                        }
                    }
                }
                l.unlock();
            } catch (Exception e) {
//                Rollback();
                try {
                    connection.rollback();
                    System.out.println(trName + "rollback!");
                } catch (Exception ex) {
                    System.out.println(trName + "rollback failed!");
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        stopConnection();//释放数据库连接
    }
}
