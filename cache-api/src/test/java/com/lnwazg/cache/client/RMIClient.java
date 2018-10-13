package com.lnwazg.cache.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.apache.commons.lang3.time.StopWatch;

import com.lnwazg.cache.rmi.IRemoteCache;

/** 
  * 客户端测试，在客户端调用远程对象上的远程方法，并返回结果。 
  */
public class RMIClient
{
    public static void main(String args[])
    {
        try
        {
            //在RMI服务注册表中查找名称为RHello的对象，并调用其上的方法 
            IRemoteCache remoteCache = (IRemoteCache)Naming.lookup("rmi://127.0.0.1:5200/REMOTE_CACHE");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //                        remoteCache.put("aaa", "ttttttttttttt");
            System.out.println(remoteCache.get("aaa"));
            //            System.out.println(remoteCache.get("0"));
            //            System.out.println(remoteCache.get("100"));
            System.out.println(String.format("共花费了%s毫秒！", stopWatch.getTime()));
        }
        catch (NotBoundException e)
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
}