package com.example.mychecker.Redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
public class Tarjan {
    /**
     * 使用Tarjan算法求得有向图中的所有强连通子图
     *
     * @return 返回强连通子图结点
     */
    private int numOfNode;  //节点数
    private List< ArrayList<Integer> > graph;    //图
    private List< ArrayList<Integer> > result;   //保存极大强连通图
    private boolean[] inStack;   //表示节点是否在栈内，因为在stack中寻找一个节点不方便。这种方式查找快
    private Stack<Integer> stack;
    private int[] dfn;
    private int[] low;
    private int time;    //time表示在栈中的编号

    public Tarjan(List< ArrayList<Integer> > graph, int numOfNode){  //带参构造
        this.graph = graph;
        this.numOfNode = numOfNode;
        this.inStack = new boolean[numOfNode];
        this.stack = new Stack<Integer>();  //栈中元素都为整数
        dfn = new int[numOfNode];
        low = new int[numOfNode];
        Arrays.fill(dfn, -1);   //将dfn所有元素都置为-1，其中dfn[i]=-1代表节点i还有没被访问过
        Arrays.fill(low, -1);
        result = new ArrayList<ArrayList<Integer>>();
    }

    public List< ArrayList<Integer> > run(){
        for(int i=0;i<numOfNode;i++){
            if(dfn[i] == -1){
                tarjan(i);
            }
        }
        return result;
    }

    //算法的主要代码
    public void tarjan(int current){  //代表第几个点在处理，递归的是点
        dfn[current] = low[current] = time++;   //新的点首先初始化
        inStack[current] = true;  //表示在栈里
        stack.push(current);  //进栈

        for(int i=0; i<graph.get(current).size(); i++){  //搜索相连节点
            int next = graph.get(current).get(i);
            if(dfn[next] == -1){                         //如果没被访问过（-1代表没有被访问）
                tarjan(next);                            //递归调用
                low[current]=Math.min(low[current], low[next]);  //更新所能到的上层节点（涉及到强连通分量子树最小根的事情)
            }else if(inStack[next]){   //如果在栈中,并且被访问过
                low[current]=Math.min(low[current], dfn[next]);  //到栈中最上端的节点
            }
        }

        if(low[current] == dfn[current]){  //发现是整个强连通分量子树里的最小根
            ArrayList<Integer> temp = new ArrayList<Integer>();
            int j = -1;
            while(current!=j){
                j = stack.pop();     //出栈，并且输出
                inStack[j] = false;   //修改状态为不在栈中
                temp.add(j);
            }
            result.add(temp);
        }
    }
}
