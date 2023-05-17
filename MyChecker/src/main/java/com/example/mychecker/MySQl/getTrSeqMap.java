package com.example.mychecker.MySQl;

public class getTrSeqMap {
    /**
     * 解析各session的执行历史
     *
     * @return 返回各session的执行历史
     */
    private int sessionNum;
    private int trNum;
    private String trace;

    public getTrSeqMap(String trace, int sessionNum, int trNum){
        this.trace = trace.replaceAll(" ", "");
        this.sessionNum = sessionNum;
        this.trNum = trNum;
    }

    public String[] sessionTrace(){
        String[] st = new String[sessionNum];
        String[] trTrace = trace.split("\n");
        for(int i = 0; i < trTrace.length; i++){
            String trName = trTrace[i].split(":")[0];
            int idx;
            if(Integer.parseInt(trName.substring(1)) % trNum == 0){
                idx = Integer.parseInt(trName.substring(1)) / trNum - 1;
            }else{
                idx = Integer.parseInt(trName.substring(1)) / trNum;
            }
            String[] op = trTrace[i].split(":")[1].split("\\),");
            for(int j = 0; j < op.length; j++){
                if(j != op.length - 1) {
                    if (st[idx] != null)
                        st[idx] += "->" + op[j].replaceAll("\\(", "(" + trName + ",") + ")";
                    else
                        st[idx] = op[j].replaceAll("\\(", "(" + trName + ",") + ")";
                }
                else{
                    if (st[idx] != null)
                        st[idx] += "->" + op[j].replaceAll("\\(", "(" + trName + ",");
                    else
                        st[idx] = op[j].replaceAll("\\(", "(" + trName + ",");
                }
            }
        }
        return st;
    }
}
