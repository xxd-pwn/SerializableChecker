package com.example.mychecker.MySQl;

import static java.lang.Thread.sleep;
public class MultiThreadTr {
    /**
     * 使用多线程向数据库管理系统注入一定量的事务
     *
     * @return 返回所有事务的trace
     */
    public static String RunAndGetTrace(int sessionNum, int trNum, int opNum, String IsolationLevel) throws InterruptedException {
        //实例化一个对象进行数据库的初始化
        Session test0 = new Session(1,0, "S0", "Serializable");
        test0.create();
        test0.interrupt();
        //使用多线程注入事务，trNum为事务的数量，opNum为每个事务进行的随机操作数量
        String trace = null;
        for(int i = 1; i <= sessionNum; i++){
            new Session(trNum, opNum, "S" + i, IsolationLevel).start();
        }
        while(true) {
            if (!CheckProcess.findProcess()) {
                trace = Session.getTrace().replaceAll("nil、", "");
                trace = trace.replaceAll("\\[nil]", "nil");
                System.out.println("打印trace如下：");
                System.out.println(trace);
                Session.ResetTrace();
                break;
            }
            sleep(10);
        }
        return trace;
    }

}
