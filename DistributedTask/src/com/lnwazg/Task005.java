package com.lnwazg;

import java.util.List;
import java.util.Map;

import com.lnwazg.api.DistributedTask;
import com.lnwazg.bean.HandleResult;

/**
 * 合力计算1到10000
 * @author nan.li
 * @version 2017年7月14日
 */
public class Task005 extends DistributedTask
{
    @Override
    public void executeCustom(Map<String, Object> map)
    {
        //汇总计算结果
        //散列计算
        int sum = 0;
        int start = 1;//1
        int end = totalTaskNum;//10000
        for (int i = start; i <= end; i++)
        {
            //i=1 totalNodes=2  %=1   nodeNum=[0,1]
            //i=2 totalNodes=2  %=0   nodeNum=[0,1]
            //i=3 totalNodes=2  %=1   nodeNum=[0,1]
            
            //i=1 totalNodes=3  %=1   nodeNum=[0,1,2]
            //i=2 totalNodes=3  %=2   nodeNum=[0,1,2]
            //i=3 totalNodes=3  %=0   nodeNum=[0,1,2]
            if (i % totalNodes  == nodeNum )
            {
                sum += i;
            }
        }
        //上报计算结果
        report("sum", sum);
    }
    
    @Override
    public HandleResult reducer(Map<String, List<HandleResult>> handleResultsMap)
    {
        int sum = 0;
        for (String nodeNum : handleResultsMap.keySet())
        {
            List<HandleResult> handleResults = handleResultsMap.get(nodeNum);
            //因为只有1步上送，因此只要取出第1步的上送结果即可！
            sum += Integer.valueOf(handleResults.get(0).getParamMap().get("sum"));
        }
        return new HandleResult().setResult(sum);
    }
    
    public int getTotalTaskNum()
    {
        return 10000;
    }
    
    public String getTaskDescription()
    {
        return "合力计算1到10000";
    }
}
