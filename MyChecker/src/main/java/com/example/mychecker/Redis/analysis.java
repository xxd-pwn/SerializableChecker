package com.example.mychecker.Redis;

import java.util.*;
public class analysis {
    /**
     * 分析数据库事务的trace并判断事务的执行是否可序列化
     *
     */
    private String Details;//集成详细的测试结果
    private String ResultPrinfln;//集成简要的测试结果
    private int MinCircleNodeNum = -1;
    private String MinCircle;
    private String Interpretation;
    private String MinCircleDep;
    private Boolean serializable = true;//判断是否可序列化的变量
    private String[] transaction;
    private int transactionNum;
    private int num;
    private Map datamap = new HashMap();
    private Map depMap = new HashMap();//key为事务的依赖关系，value为推断出该依赖关系的具体两个操作
    private List<ArrayList<Integer>> graph  = new ArrayList<ArrayList<Integer>>();
    private String[][] dependencyType;
    public analysis(int TrNum){
        this.transactionNum = TrNum;
        String[][] dependencyType = new String[TrNum + 1][TrNum + 1];
        this.dependencyType = dependencyType;
    }

    //数据预处理及存储数据
    private void DataPreprocessing(String trace){
        //传入数据格式：
        /*
        String trace = "T1: r(2432, [10]), r(2434, nil)\n" +
                "T2: w(2434, 10)\n" +
                "T3: w(2432, 10), r(2434, [10])";
        */
        //去除空格
        trace = trace.replace(" ", "");
//        System.out.println("原始数据：");
//        System.out.println(trace);
        //按行存入数组transaction
        this.transaction = trace.split("\\n");

        //使用map存储数据，kay为对象地址addr，value为该地址下的所有操作
        for (int i = 0; i < transaction.length; i++) {
            //System.out.println(transaction[i]);
            //每个transaction数组存储内容类似T1:r(2432,[10]),r(2434,nil)这一步取出事务名tr
            String tr = transaction[i].split(":")[0];
            //这里取出当前事务的所有操作存入operat数组，最后一个操作为r(2434,nil)，其余操作为r(2432,[10]，缺少右括号
            String[] operat = transaction[i].split(":")[1].split("\\),");
            for (int j = 0; j < operat.length; j++) {//遍历事务里每个operation
                //operat[j].split(",")[0] = r(2434，类似这样的结构
                String addr = operat[j].split(",")[0].split("\\(")[1];//取出地址addr

                //下面的操作将取出来的所有操作及地址存入map中
                if (j != operat.length - 1) {
                    if (!datamap.containsKey(addr))
                        datamap.put(addr, operat[j] + "," + tr + ")");
                    else
                        datamap.put(addr, datamap.get(addr) + operat[j] + "," + tr + ")");
                } else {//这种情况遍历到最后一个操作，要去除右括号，加上事务名信息再加上右括号
                    if (!datamap.containsKey(addr))
                        datamap.put(addr, operat[j].split("\\)")[0] + "," + tr + ")");
                    else
                        datamap.put(addr, datamap.get(addr) + operat[j].split("\\)")[0] + "," + tr + ")");
                }
            }
        }

        //使用邻接表的方式存储依赖图
        int numOfNode = this.transactionNum + 1;
        for (int i = 0; i < numOfNode; i++) {
            graph.add(new ArrayList<Integer>());
        }
    }

    /*检查non-cycle anomalies——internal inconsistencies*/
    private void CheckInternalInconsistency(){
        for(int i = 0; i < transaction.length; i++){
            String[] TrOpera = transaction[i].split(":")[1].split("\\),");
            for(int j = 0; j < TrOpera.length; j++){
                String optype1 = TrOpera[j].split("\\(")[0];
                if(optype1.equals("w")){
                    String addr1 = TrOpera[j].split(",")[0].split("\\(")[1];
                    int k = j + 1;
                    if(k < TrOpera.length){
                        //查找后面的所有r操作，看看同地址的读操作是否有读到之前写入的内容
                        for(; k < TrOpera.length; k++){
                            String addr2 = TrOpera[k].split(",")[0].split("\\(")[1];
                            String optype2 = TrOpera[k].split("\\(")[0];
                            if(optype2.equals("r") && addr1.equals(addr2)){
                                String content1 = TrOpera[j].split(",")[1].replace(")", "");
                                if(TrOpera[k].contains(content1)){
                                    break;//后面的读无需检查，肯定满足
                                }
                                else{
                                    System.out.println("发现内部不一致错误：" + TrOpera[j] + ")-Tr->"
                                            + TrOpera[k].replace(")", "") + ")");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //将一个地址中所有操作按照版本顺序进行排序
    private String[] GetOrder(String allop){
        //先进行读排序
        String[] oparr = allop.split("\\)");
        String[] order = new String[oparr.length];//存储排好序的操作
        this.num = 0;//记录排好序的操作数量
        for (int i = 0; i < oparr.length; i++) {
            String optype = oparr[i].split("\\(")[0];
            if (optype.equals("r")) {//读操作，类似r(addr,[],T1)
                String readContent = oparr[i].split(",")[1];//取出读的内容
                if (readContent.equals("nil")) {
                    //将order里面所有元素往后挪一位
                    for (int k = num; k >= 1; k--) {
                        order[k] = order[k - 1];
                    }
                    order[0] = oparr[i] + ")";
                    num++;
                } else {
                    if (num == 0) {
                        order[num++] = oparr[i] + ")";
                    } else {//寻找该读操作的后继读操作
                        for (int k = 0; k < num; k++) {
                            String readCont = order[k].split(",")[1];
                            if (readCont.equals("nil")) {//对nil情况特殊分析
                                if (k == num - 1) {
                                    order[num++] = oparr[i] + ")";
                                    break;
                                } else
                                    continue;
                            }
                            //比较读内容长度判断先后顺序
                            if (readContent.length() <= readCont.length()) {
                                for (int j = num; j >= k + 1; j--) {
                                    order[j] = order[j - 1];
                                }
                                order[k] = oparr[i] + ")";
                                num++;
                                break;
                            }
                            if (k == num - 1) {//说明该读操作是order里面目前最靠后的操作，放最后
                                order[num++] = oparr[i] + ")";
                                break;
                            }
                        }
                    }
                }
            }
        }
//               for(int i = 0; i < num; i++){
//                   System.out.print(order[i] + " ");
//               }
//               System.out.println();
        //进行写排序
        for (int i = 0; i < oparr.length; i++) {
            String optype = oparr[i].split("\\(")[0];
            if (optype.equals("w")) {
                String writeContent = oparr[i].split(",")[1];
                int index = -1;
                for (int j = 0; j < num; j++) {
                        /*在order里面寻找包含写操作内容的读操作，写操作为其前继;
                        如果找不到对应读操作说明该写操作未被观察到，无法推断依赖，舍弃*/
                    boolean judge = false;
                    if (order[j].split(",")[1].contains(writeContent)){
                        String readcont0 = order[j].split(",")[1];
                        readcont0 = readcont0.replaceAll("\\[", "");
                        readcont0 = readcont0.replaceAll("]", "");
                        String[] rt0 = readcont0.split("、");
                        for(int d = 0; d < rt0.length; d++){
                            if(rt0[d].equals(writeContent)){
                                judge = true;
                                break;
                            }
                        }
                    }
                    if (judge) {
                        //System.out.println(order[j].split(",")[1]);
                        index = j;//记录在该写操作前面的连续写操作的最小下标

                        for (int k = j - 1; k >= 0; k--) {
                            if (order[k].split("\\(")[0].equals("w")) {
                                index = k;
                            } else
                                break;
                        }
                        for (int k = num; k >= j + 1; k--) {
                            order[k] = order[k - 1];
                        }
                        num++;
                        order[j] = oparr[i] + ")";

                        //使用冒泡排序算法对index到j处的连续写操作排序
                        for (int n = index; n <= j; n++) {
                            for (int m = index; m < j; m++) {
                                //order[j + 1].indexOf(order[m].split(",")[1])为该写操作的内容在后继read中的下标，越大说明顺序越靠后
                                String readcont = order[j + 1].split(",")[1];
                                readcont = readcont.replaceAll("\\[", "");
                                readcont = readcont.replaceAll("]", "");
                                String[] rt = readcont.split("、");
                                int index_1 = -1;
                                int index_2 = -1;
                                for(int d = 0; d < rt.length; d++){
                                    if(index_1 == -1 && rt[d].equals(order[m].split(",")[1])){
                                        index_1 = d;
                                        break;
                                    }
                                }
                                for(int d = 0; d < rt.length; d++){
                                    if(index_2 == -1 && rt[d].equals(order[m + 1].split(",")[1])){
                                        index_2 = d;
                                        break;
                                    }
                                }
                                if (index_1 > index_2) {
                                    String tmp = order[m];
                                    order[m] = order[m + 1];
                                    order[m + 1] = tmp;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        return order;
    }

    //从操作中提取依赖
    private void InferTheDependencies(String[] order){
        //将依赖关系提取并存入表中
        for (int i = 0; i < num; i++) {
            if (i != num - 1) {
                String optype1 = order[i].split("\\(")[0];//操作类型
                String optype2 = order[i + 1].split("\\(")[0];
                int tr1 = Integer.parseInt((order[i].split(",")[2].split("\\)")[0]).substring(1));//取出事务序号
                int tr2 = Integer.parseInt((order[i + 1].split(",")[2].split("\\)")[0]).substring(1));
                if (optype1.equals("r")) {
                    //考虑rw这种情况
                    if (optype2.equals("w")) {
                        //System.out.print(tr1 + "-rw->" + tr2 +" ");
                        if(dependencyType[tr1][tr2] == null && tr1 != tr2 && !graph.get(tr1).contains(tr2))
                        {
                            graph.get(tr1).add(tr2);//事务tr1到事务tr2之间存在依赖关系
                            String optmp1 = order[i].replaceAll(",T" + tr1, "").replaceAll("\\(", "(T" + tr1 + ",");
                            String optmp2 = order[i + 1].replaceAll(",T" + tr2, "").replaceAll("\\(", "(T" + tr2 + ",");
                            depMap.put(tr1 + "->" + tr2, optmp1 + "->" + optmp2);
                            if (dependencyType[tr1][tr2] == null)
                                dependencyType[tr1][tr2] = "rw";
                            else
                                dependencyType[tr1][tr2] = "rw";//为方便后面分析具体异常类型只记录一种依赖
                        }
                    }
                    //考虑rrrr...w这种情况，如果是只有r操作，则不存在依赖关系
                    else if (optype2.equals("r")) {
                        int w_index = -1;//记录一系列连续读操作的下一个版本写操作下标
                        for (int j = i + 1; j < num; j++) {
                            if (order[j].split("\\(")[0].equals("w")) {
                                w_index = j;
                                break;
                            }
                        }
                        if (w_index == -1) {//说明为rrrrr...这种情况
                            break;
                        }
                        else {//说明为rrrr...w这种情况
                            int tr4 = Integer.parseInt((order[w_index].split(",")[2].split("\\)")[0]).substring(1));//取出事务序号
                            for (int begin = i; begin < w_index; begin++) {
                                int tr3 = Integer.parseInt((order[begin].split(",")[2].split("\\)")[0]).substring(1));
                                if(dependencyType[tr3][tr4] == null && tr3 != tr4 && !graph.get(tr3).contains(tr4))
                                {
                                    graph.get(tr3).add(tr4);
                                    //System.out.print(tr3 + "-rw->" + tr4 +" ");
                                    String optmp1 = order[begin].replaceAll(",T" + tr3, "").replaceAll("\\(", "(T" + tr3 + ",");
                                    String optmp2 = order[w_index].replaceAll(",T" + tr4, "").replaceAll("\\(", "(T" + tr4 + ",");
                                    depMap.put(tr3 + "->" + tr4, optmp1 + "->" + optmp2);
                                    if (dependencyType[tr3][tr4] == null)
                                        dependencyType[tr3][tr4] = "rw";
                                    else
                                        dependencyType[tr3][tr4] = "rw";
                                }
                            }
                            i = w_index - 1;
                        }
                    }
                }
                else if (optype1.equals("w")) {
                    //考虑ww情况
                    if (optype2.equals("w")) {
                        //System.out.print(tr1 + "-ww->" + tr2 +" ");
                        if(dependencyType[tr1][tr2] == null && tr1 != tr2 && !graph.get(tr1).contains(tr2)) {
                            graph.get(tr1).add(tr2);
                            String optmp1 = order[i].replaceAll(",T" + tr1, "").replaceAll("\\(", "(T" + tr1 + ",");
                            String optmp2 = order[i + 1].replaceAll(",T" + tr2, "").replaceAll("\\(", "(T" + tr2 + ",");
                            depMap.put(tr1 + "->" + tr2, optmp1 + "->" + optmp2);
                            if (dependencyType[tr1][tr2] == null)
                                dependencyType[tr1][tr2] = "ww";
                            else
                                dependencyType[tr1][tr2] = "ww";//为方便后面分析具体异常类型只记录一种依赖
                        }
                    }
                    //考虑wrrrrr...情况
                    else if (optype2.equals("r")) {
                        int w_index = -1;//记录一系列连续读操作的下一个版本写操作下标
                        for (int j = i + 1; j < num; j++) {
                            if (order[j].split("\\(")[0].equals("w")) {
                                w_index = j;
                                break;
                            }
                        }
                        if (w_index == -1) {//说明为wrrrrr...这种情况
                            int tr3 = Integer.parseInt((order[i].split(",")[2].split("\\)")[0]).substring(1));//取出事务序号
                            for (int begin = i + 1; begin < num; begin++) {
                                int tr4 = Integer.parseInt((order[begin].split(",")[2].split("\\)")[0]).substring(1));
                                if(dependencyType[tr3][tr4] == null && tr3 != tr4 && !graph.get(tr3).contains(tr4)){
                                    graph.get(tr3).add(tr4);
                                    String optmp1 = order[i].replaceAll(",T" + tr3, "").replaceAll("\\(", "(T" + tr3 + ",");
                                    String optmp2 = order[begin].replaceAll(",T" + tr4, "").replaceAll("\\(", "(T" + tr4 + ",");
                                    depMap.put(tr3 + "->" + tr4, optmp1 + "->" + optmp2);
                                    //System.out.print(tr3 + "-wr->" + tr4 +" ");
                                    if (dependencyType[tr3][tr4] == null)
                                        dependencyType[tr3][tr4] = "wr";
                                    else
                                        dependencyType[tr3][tr4] = "wr";
                                }
                            }
                            break;
                        } else {//说明为wrrrr...w这种情况
                            int tr3 = Integer.parseInt((order[i].split(",")[2].split("\\)")[0]).substring(1));//取出事务序号
                            for (int begin = i + 1; begin < w_index; begin++) {
                                int tr4 = Integer.parseInt((order[begin].split(",")[2].split("\\)")[0]).substring(1));
                                if(dependencyType[tr3][tr4] == null && tr3 != tr4 && !graph.get(tr3).contains(tr4)) {
                                    graph.get(tr3).add(tr4);
                                    String optmp1 = order[i].replaceAll(",T" + tr3, "").replaceAll("\\(", "(T" + tr3 + ",");
                                    String optmp2 = order[begin].replaceAll(",T" + tr4, "").replaceAll("\\(", "(T" + tr4 + ",");
                                    depMap.put(tr3 + "->" + tr4, optmp1 + "->" + optmp2);
                                    //System.out.print(tr3 + "-wr->" + tr4 +" ");
                                    if (dependencyType[tr3][tr4] == null)
                                        dependencyType[tr3][tr4] = "wr";
                                    else
                                        dependencyType[tr3][tr4] = "wr";
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //根据定义分析异常类型
    private void Analyse(String[] node){
        int[] count = new int[3];//分别记录rw ， wr， ww依赖数量
        for (int n = 0; n < node.length; n++) {
            for (int m = 0; m < node.length; m++) {
                if (n == m)
                    continue;
                else {
                    if (dependencyType[Integer.parseInt(node[n])][Integer.parseInt(node[m])] != null) {
                        if (dependencyType[Integer.parseInt(node[n])][Integer.parseInt(node[m])].equals("rw")) {
                            count[0]++;
                            MinCircleDep += Integer.parseInt(node[n]) + "-rw->" + Integer.parseInt(node[m]) + " ";
                        }
                        if (dependencyType[Integer.parseInt(node[n])][Integer.parseInt(node[m])].equals("wr")) {
                            count[1]++;
                            MinCircleDep += Integer.parseInt(node[n]) + "-wr->" + Integer.parseInt(node[m]) + " ";
                        }
                        if (dependencyType[Integer.parseInt(node[n])][Integer.parseInt(node[m])].equals("ww")) {
                            count[2]++;
                            MinCircleDep += Integer.parseInt(node[n]) + "-ww->" + Integer.parseInt(node[m]) + " ";
                        }
                    }
                }
            }
        }
        MinCircleDep = MinCircleDep.replaceAll("null", "");
        System.out.println(MinCircleDep);
        //for(int k = 0; k < count.length; k++){
        //                System.out.println(count[g]);
        if (count[0] == 0 && count[1] == 0) {//0:rw;1:wr;2:ww
            //G0: A cycle comprised entirely of write-write edges.
            Interpretation = "Type:G0-anomaly（脏写隔离异常）\n" + "解释：依赖关系图仅包含ww依赖边组成的环\n";
            System.out.println(Interpretation);
        } else if (count[0] == 0) {
            //A cycle comprised of write-write or write-read edges.
            Interpretation = "Type:G1C-anomaly（脏读隔离异常）\n" + "解释：依赖关系图包含由ww和wr依赖边组成的环\n";
            System.out.println(Interpretation);
        } else if (count[0] == 1) {
            //A cycle with exactly one read-write edge.
            Interpretation = "Type:G-Single-anomaly（不可重复读隔离异常）\n" + "解释：依赖关系图包含一个正好有一条rw依赖边的有向循环\n";
            System.out.println(Interpretation);
        } else if (count[0] > 1) {
            //A cycle with one or more read-write edges.
            Interpretation = "Type:G2-anomaly（幻读隔离异常）\n" + "解释：依赖关系图中包含由一个或多个rw依赖边组成的有向循环\n";
            System.out.println(Interpretation);
        }
        else{
            System.out.println("Unknown-anomaly");
        }
    }

    public void check(String trace) {
        //数据预处理
        DataPreprocessing(trace);

        //检查non-cycle anomalies——internal inconsistencies
        CheckInternalInconsistency();

        System.out.println("数据处理后：");
        Iterator iter = datamap.entrySet().iterator();
        //排序及建立依赖
        System.out.println("按版本顺序排序：");
        String orderedOp = "";//存储排好序的操作，输出展示
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();//地址
            Object val = entry.getValue();//操作集合
//            System.out.println(key + ":" + val);

            String allop = (String) val;
            String[] order = GetOrder(allop);

            //打印按版本顺序排序的操作
            if(order[0] != null){
                orderedOp += key + ":";
                for(int i = 0; i < num; i++){
                    orderedOp += order[i] + " ";
                }
                orderedOp += "\n";
            }

            //将依赖关系提取并存入表中
            InferTheDependencies(order);
        }
        //输出排好序的操作
        System.out.println(orderedOp);
        Details = "完整的Trace：\n" + trace + "按版本顺序排好序的所有操作如下：\n" + orderedOp;
//        this.ReOpSeq = orderedOp;

        //以邻接表的形式输出依赖图
        String PrintDependence = "";
        for(int i = 0; i < graph.size(); i++){
            if(graph.get(i).size() > 0){
                PrintDependence += i + ":";
                for (int j = 0; j < graph.get(i).size(); j++) {
                    PrintDependence += graph.get(i).get(j) + " " + dependencyType[i][graph.get(i).get(j)] + " ";
                }
                PrintDependence += "\n";
            }
        }
        System.out.println(PrintDependence);
        Details += "打印所有事务间的依赖关系如下：\n" + PrintDependence;

        //调用Tarjan算法求极大连通子图
        Tarjan t = new Tarjan(graph, transactionNum + 1);
        List<ArrayList<Integer>> result = t.run();  //结果存在result中

        //打印结果
        for (int i = 1; i < result.size(); i++) {
            if (result.get(i).size() > 1) {
                String PrintRsTmp = "";//存储详细测试信息的辅助变量，记录一个极大连通子图中的全部信息
                System.out.println("Tarjan算法求得的极大连通子图" + i + "如下：");
                PrintRsTmp += "Tarjan算法求得的极大连通子图" + i + "如下：";
                String nodeTarjan = "";
                for (int j = 0; j < result.get(i).size(); j++) {
                    nodeTarjan += result.get(i).get(j) + " ";
                }
                System.out.println(nodeTarjan);
                PrintRsTmp += nodeTarjan + "\n";
                serializable = false;
                //打印极大连通子图的节点间具体依赖
                for (int m = 0; m < result.get(i).size(); m++) {
                    for (int n = 0; n < result.get(i).size(); n++) {
                        if (m != n && dependencyType[result.get(i).get(m)][result.get(i).get(n)] != null)
                        {
                            System.out.print(result.get(i).get(m) + "-" + dependencyType[result.get(i).get(m)][result.get(i).get(n)] + "->" + result.get(i).get(n) + " ");
                            PrintRsTmp += result.get(i).get(m) + "-" + dependencyType[result.get(i).get(m)][result.get(i).get(n)] + "->" + result.get(i).get(n) + " ";
                        }

                    }
                }
                System.out.println();
                PrintRsTmp += "\n";

                BFS bfs = new BFS(dependencyType, nodeTarjan);
                this.MinCircle = bfs.result();
                System.out.println("bfs算法求得最小循环为:" + MinCircle);
                PrintRsTmp += "bfs算法求得最小循环为:" + MinCircle + "\n";

                MinCircleDep = "";
                //根据定义分析异常类型
                String[] node = bfs.getResNode().split(" ");
                Analyse(node);
                PrintRsTmp += MinCircleDep + "\n";
                //在详细内容里存储所有的结果
                String tmp = getNodeTrace(trace);
                Details += PrintRsTmp + "Related Trace: \n" + tmp + Interpretation + "\n" + "\n";
                //当前最优为空，储存此次结果，以找到最小的circle并返回
                if(MinCircleNodeNum == -1){
                    MinCircleNodeNum = node.length;
                    ResultPrinfln = "The minimum cycle calculated by the BFS algorithm: " + MinCircle + "\n" +
                            MinCircleDep + "\n" + "Related Trace: \n" + tmp + Interpretation + "\n";
                }
                else{
                    //说明找到更小的circle
                    if(node.length < MinCircleNodeNum){
                        MinCircleNodeNum = node.length;
                        ResultPrinfln = "The minimum cycle calculated by the BFS algorithm: " + MinCircle + "\n" +
                                MinCircleDep + "\n" + "Related Trace: \n" + tmp + Interpretation + "\n";
                    }
                }
                //说明已经找到最小的circle
                if(node.length == 2){
                    break;
                }
            }
            else{
                if(i == result.size() - 1 && serializable){
                    System.out.println("这批事务是可序列化的！");
                }
            }
        }

    }

    //筛选出组成最小循环的事务的trace
    public String getNodeTrace(String trace){
        String res = "";
        String[] tmp = trace.split("\n");
        int num = 0;
        String[] tmp2 = MinCircle.split(" ");
        Set set = new HashSet<>();
        for(int i = 0; i < tmp2.length - 1; i++){
            set.add(tmp2[i]);
        }
        int targetNum = set.size();
        for(int i = 0; i < tmp.length; i++){
            if(set.contains(tmp[i].split(": ")[0].substring(1))){
                res += tmp[i] + "\n";
                num++;
            }
            if(num == targetNum){
                break;
            }
        }
        System.out.println(res);
        return res;
    }

    public Boolean getSerializable() {
        return serializable;
    }

    public String getMinCircle() {
        return MinCircle;
    }

    public String getMinCircleDep() {
        return MinCircleDep;
    }

    public String getInterpretation() {
        return Interpretation;
    }

    public String getResultPrinfln() {
        return ResultPrinfln;
    }

    public String getDetails() {
        return Details;
    }
    public String[][] getDependencyType() {
        return dependencyType;
    }

    public List<ArrayList<Integer>> getGraph() {
        return graph;
    }
    public Map getDepMap() {
        return depMap;
    }

}
