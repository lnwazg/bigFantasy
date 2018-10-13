package com.lnwazg;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.lnwazg.api.DistributedTask;
import com.lnwazg.bean.HandleResult;
import com.lnwazg.kit.log.Logs;

/**
 * 计算一个txt文件里面的数据总和<br>
 * 1.采用总行数指引法<br>
 * 2.采用自我决策已经到结束法<br>
 * 
 * 1.需要一个总行数
 * 遍历累加，直到超过了总任务数
 * 
 * 2.需要每次判断是否已经到达文末
 * 该方案不需要依赖总行数的计算！
 * 
 * Task003采用方案2
 * @author nan.li
 * @version 2017年7月14日
 */
public class Task003 extends DistributedTask
{
    /**
     * 工作文件
     */
    String workFilePath = "D:\\1.txt";
    
    File file = new File(workFilePath);
    
    List<String> list = null;
    
    {
        try
        {
            list = FileUtils.readLines(file, CharEncoding.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    protected ImmutablePair<Boolean, Long> calcTaskCustom(long taskNum)
    {
        if (taskNum > list.size() - 1)
        {
            //若任务已经超出计算范围，则返回false
            return new ImmutablePair<Boolean, Long>(false, null);
        }
        else
        {
            return new ImmutablePair<Boolean, Long>(true, Long.valueOf(list.get((int)taskNum)));
        }
    }
    
    @Override
    public void executeCustom(Map<String, Object> map)
    {
        //汇总计算结果
        long sum = 0;
        long i = 0;
        while (true)
        {
            //任务号
            //假如当前是0号节点，总计10节点，那么当前任务号就是0，下一个任务号是10，下下个就是20
            long taskNum = nodeNum + totalNodes * i;
            @SuppressWarnings("unchecked")
            ImmutablePair<Boolean, Long> pair = (ImmutablePair<Boolean, Long>)calcTask(taskNum);
            if (pair.getLeft())
            {
                //还没超过
                //计算当前的任务结果
                sum += pair.getRight();
            }
            else
            {
                Logs.d("任务已经超出计算范围，因此准备结束！");
                break;
            }
            i++;
        }
        
        //上报计算结果
        report("sum", sum);
    }
    
    @Override
    public HandleResult reducer(Map<String, List<HandleResult>> handleResultsMap)
    {
        long sum = 0;
        for (String nodeNum : handleResultsMap.keySet())
        {
            List<HandleResult> handleResults = handleResultsMap.get(nodeNum);
            //因为只有1步上送，因此只要取出第1步的上送结果即可！
            sum += Long.valueOf(handleResults.get(0).getParamMap().get("sum"));
        }
        return new HandleResult().setResult(sum);
    }
    
    public String getTaskDescription()
    {
        return "计算txt文本中的数字总和";
    }
}
