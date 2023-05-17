package com.example.mychecker.Oracle;

import java.util.Queue;
import java.util.LinkedList;
public class BFS {
    /**
     * 使用BFS算法求得有向图中的一个最小循环
     *
     * @return 以节点形式返回最小循环
     */
    private String[] node_tarjan;
    private String[][] graph;
    private String resNode;
    public BFS(String[][] graph, String node_tarjan){
        this.graph = graph;
        this.node_tarjan = node_tarjan.split(" ");
    }
    //返回组成最小循环的节点（例如：4 3）
    public String getResNode(){
        return this.resNode;
    }
    //返回最小循环（例如：4 3 4）
    public String result(){
        String res = "";
        Queue<String> q = new LinkedList<String>();//存储搜索节点的队列
        for(int t = 0; t < node_tarjan.length; t++){
            //以node_tarjan[t]为开始节点
            q.add(node_tarjan[t] + " ");
//        System.out.println("开始节点：" + node_tarjan[0]);
            while(q.size() != 0){
                int len = q.size();
                String[] tmp = new String[len];//存储队列中的元素
                int tmpNum = 0;
                while(!q.isEmpty()){//队列中所有元素依次出队
                    tmp[tmpNum++] = q.poll();
                }
                for(int i = 0; i < tmpNum; i++){
                    //当前搜索节点数已经大于当前最优解，剪枝
                    if(!res.equals("")&& tmp[i].split(" ").length + 1 >= res.split(" ").length){
                        continue;
                    }
                    //这种情况说明已经找到最小circle，直接返回
                    if(!res.equals("")&& res.split(" ").length == 3){
                        return res;
                    }
                    //tmp[i]是当前搜索路径，比如1 2 3, 中间用空格隔开，node是当前路径选择节点，即最后一个节点
                    int node = Integer.parseInt(tmp[i].split(" ")[tmp[i].split(" ").length - 1]);
                    for(int j = 0; j < node_tarjan.length; j++){
                        int nextN = Integer.parseInt(node_tarjan[j]);
                        if(nextN != node && graph[node][nextN] != null){
                            String[] existNode = tmp[i].split(" ");//存储已经搜索过的节点
                            boolean findCircle = false;
                            String res2 = "";
                            for(int k = 0; k < existNode.length; k++){
                                if(findCircle == true){
                                    res2 += " " + existNode[k];
                                }
                                if(existNode[k].equals(String.valueOf(nextN))){//说明找到一个circle
                                    findCircle = true;
                                    res2 = existNode[k];
                                }
                            }
                            if(findCircle){//说明找到一个circle
                                if(res.equals("")){
                                    res = res2 + " " + String.valueOf(nextN);
                                    this.resNode = res2;
                                }
                                else if((res2 +  " " + String.valueOf(nextN)).length() < res.length()){
                                    res = res2 + " " + String.valueOf(nextN);
                                    this.resNode = res2;
                                }
                            }
                            else {
                                String newtry = tmp[i] + nextN + " ";
                                q.add(newtry);
                            }
                        }
                    }
                }
//            System.out.println("q = " + q);
            }
        }
        return res;
    }
}
