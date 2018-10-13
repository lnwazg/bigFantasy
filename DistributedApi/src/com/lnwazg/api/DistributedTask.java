package com.lnwazg.api;

/**
 * 分布式任务
 * @author nan.li
 * @version 2017年7月6日
 */
public interface DistributedTask
{
    /**
     * 执行任务的方法
     * @author nan.li
     */
    void execute();
    
    /**
     * 获取任务的描述信息
     * @author nan.li
     * @return
     */
    String getTaskDescription();
}
