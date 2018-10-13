package com.lnwazg.cache.client;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

import com.lnwazg.cache.proxy.Cache;
import com.lnwazg.cache.rmi.IRemoteCache;
import com.lnwazg.kit.io.StreamUtils;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.property.PropertyUtils;

public class RemoteCacheServer
{
    /**
    * 默认的配置文件路径
    */
    public static final String DEFAULT_CONFIG_FILEPATH = "remoteCache.properties";
    
    /**
     * MQ服务器地址
     */
    public static String SERVER = "";
    
    /**
     * MQ端口号
     */
    public static String PORT = "";
    
    public static Cache initDefaultConfig()
    {
        return initConfig(DEFAULT_CONFIG_FILEPATH);
    }
    
    private static Cache initConfig(String configPath)
    {
        Map<String, String> configs = null;
        InputStream inputStream = RemoteCacheServer.class.getClassLoader().getResourceAsStream(configPath);
        if (inputStream != null)
        {
            try
            {
                configs = PropertyUtils.load(inputStream);
                if (configs.isEmpty())
                {
                    Logs.e(String.format("配置文件%s内容为空！因此无法初始化RemoteCacheServer config！", configPath));
                    return null;
                }
            }
            finally
            {
                StreamUtils.close(inputStream);
            }
        }
        else
        {
            Logs.e(String.format("配置文件%s不存在！因此无法初始化RemoteCacheServer config！", configPath));
            return null;
        }
        SERVER = configs.get("SERVER");
        PORT = configs.get("PORT");
        return initConfig(SERVER, Integer.valueOf(PORT));
    }
    
    /**
     * 初始化客户端，业务Id为空，将使用全局空间
     * @author nan.li
     * @param server
     * @param port
     * @param myselfAddress
     * @param channels
     */
    public static Cache initConfig(String server, int port)
    {
        try
        {
            //在RMI服务注册表中查找名称为RHello的对象，并调用其上的方法 
            IRemoteCache remoteCache = (IRemoteCache)Naming.lookup(String.format("rmi://%s:%s/REMOTE_CACHE", server, port));
            return new Cache(remoteCache);
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
        return null;
    }
    
    /**
     * 初始化客户端，并指定业务Id
     * @author nan.li
     * @param server 服务器
     * @param port  端口号
     * @param bid   业务Id
     * @return
     */
    public static Cache initConfig(String server, Integer port, String bid)
    {
        try
        {
            //在RMI服务注册表中查找名称为RHello的对象，并调用其上的方法 
            IRemoteCache remoteCache = (IRemoteCache)Naming.lookup(String.format("rmi://%s:%s/REMOTE_CACHE", server, port));
            return new Cache(remoteCache, bid);
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
        return null;
    }
}
