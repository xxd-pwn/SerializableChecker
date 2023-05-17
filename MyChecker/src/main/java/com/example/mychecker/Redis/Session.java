package com.example.mychecker.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.sql.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Session extends Thread{
    /**
     * 生成事务并注入Redis数据库执行的线程
     *
     */
    private Jedis jedis;
    private int trNum;
    private int num;
    private String name;
    private int writeContent = 0;
    static private String trace = null;//记录事务的trace
    private static Lock l = new ReentrantLock();//用于加锁修改事务trace的锁
    public static String getTrace(){
        return trace;
    }
    public static void ResetTrace(){
        trace = null;
    }
    //将数据库恢复到初始状态,所有对象内容置为nil
    public void initial() {
        //update，清空测试用的100个对象的内容
        for (int i = 1; i <= 100; i++) {
            jedis.set(String.valueOf(i), "nil");
        }
    }

    //redis数据库连接并初始化参数，事务的操作数，名称与隔离级别
    public Session(int trnum, int num, String name){
        this.trNum = trnum;
        this.num = num;
        this.name = name;
        this.jedis = new Jedis("127.0.0.1", 6379);;
        this.writeContent = (Integer.parseInt(name.substring(1)) - 1)  * (trnum * num) +  1;
    }

    //获取1-3的随机数
    private int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


    public void run() {
        int s = Integer.parseInt(this.name.substring(1));
        //一个session内的事务串行执行
        for(int t = 1; t <= trNum; t++) {
            String trName = "T" + ((s - 1) * trNum + t);
            String[] wcontent = new String[num + 1];
            String[] opaddr = new String[num + 1];
            String[] optype = new String[num + 1];
            int wcontentIdx = 0, opaddrIdx = 0, optypeIdx = 0;
            //开启一个redis事务
            Transaction transaction = jedis.multi();
            String trace1 = null;
            List<Object> list;
            try {
                for (int i = 0; i < num; i++) {
                    int n = getRandomNumberInRange(1, 3);
                    //使用随机数来生成随机操作，1为跳过，2为写操作，3为读操作
                    if (n == 1) {
//                    System.out.println(this.name + "什么都不执行");
                    }
                    //append
                    else if (n == 2) {
                        optype[optypeIdx++] = "w";
                        String addr = String.valueOf(getRandomNumberInRange(1, 5));
//                        String addr = "1";
                        opaddr[opaddrIdx++] = addr;
                        String writecontent = String.valueOf(writeContent++);
                        transaction.append(addr, "、" + writecontent);//加入操作队列
                        wcontent[wcontentIdx++] = writecontent;
                    }
                    //read
                    else if (n == 3) {
                        optype[optypeIdx++] = "r";
                        String addr = String.valueOf(getRandomNumberInRange(1, 5));
//                        String addr = "1";
                        opaddr[opaddrIdx++] = addr;
                        transaction.get(addr);//加入操作队列
                    }
                }
                list = transaction.exec();//提交事务
            }catch (Exception e) {
                System.out.println(trName + "discard!");
                transaction.discard();//取消执行事务
                throw e;
            }

            int index = 0;
            int w_idx = 0;
            for(Object i : list){
                if(optype[index].equals("w")){//说明为写操作
                    if (trace1 != null)
                        trace1 += ", w(" + opaddr[index] + ", " + wcontent[w_idx] + ")";
                    else
                        trace1 = "w(" + opaddr[index] + ", " + wcontent[w_idx] + ")";
                    w_idx++;
                    index++;
                }
                else{//说明为读操作
                    if (trace1 != null)
                        trace1 += ", r(" + opaddr[index] + ", [" + String.valueOf(i) + "])";
                    else
                        trace1 = "r(" + opaddr[index] + ", [" + String.valueOf(i) + "])";

                    index++;
                }
            }

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

        }
        jedis.close();//释放数据库连接
    }

}
