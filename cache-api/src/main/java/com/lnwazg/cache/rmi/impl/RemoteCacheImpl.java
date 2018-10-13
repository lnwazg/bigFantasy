package com.lnwazg.cache.rmi.impl;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.lang3.SerializationUtils;

import com.lnwazg.cache.rmi.IRemoteCache;
import com.lnwazg.kit.cache.FileCache;
import com.lnwazg.kit.compress.GzipBytesUtils;

/** 
 * 远程的接口的实现 
 */
public class RemoteCacheImpl extends UnicastRemoteObject implements IRemoteCache
{
    private static final long serialVersionUID = 1L;
    
    FileCache fileCache;
    
    /** 
     * 因为UnicastRemoteObject的构造方法抛出了RemoteException异常，因此这里默认的构造方法必须写，必须声明抛出RemoteException异常 
     * @throws RemoteException 
     */
    public RemoteCacheImpl()
        throws RemoteException
    {
        fileCache = new FileCache("my_cache_server");
    }
    
    public RemoteCacheImpl(Integer port)
        throws RemoteException
    {
        //区分端口号，即可完美解决多租户的问题    
        //想连接到不同的redis服务，就指定不同的端口号即可
        fileCache = new FileCache(String.format("my_cache_server_%s", port));
    }
    
    @Override
    public boolean put(Serializable key, Serializable value)
        throws RemoteException
    {
        fileCache.put(key, value);
        return true;
    }
    
    @Override
    public boolean incr(Serializable key)
        throws RemoteException
    {
        byte[] valueBytes = (byte[])get(key);
        if (valueBytes != null)
        {
            valueBytes = GzipBytesUtils.unzip(valueBytes);
        }
        Object object = null;
        if (valueBytes != null)
        {
            object = SerializationUtils.deserialize(valueBytes);
        }
        //取出了原始值
        if (object instanceof Integer)
        {
            int num = (int)object;
            //将其自增1
            num++;
            //然后重新放进去
            valueBytes = SerializationUtils.serialize(num);
            valueBytes = GzipBytesUtils.zip(valueBytes);
            return put(key, valueBytes);
        }
        return false;
    }
    
    @Override
    public boolean decr(Serializable key)
        throws RemoteException
    {
        byte[] valueBytes = (byte[])get(key);
        if (valueBytes != null)
        {
            valueBytes = GzipBytesUtils.unzip(valueBytes);
        }
        Object object = null;
        if (valueBytes != null)
        {
            object = SerializationUtils.deserialize(valueBytes);
        }
        //取出了原始值
        if (object instanceof Integer)
        {
            int num = (int)object;
            //将其自增1
            num--;
            //然后重新放进去
            valueBytes = SerializationUtils.serialize(num);
            valueBytes = GzipBytesUtils.zip(valueBytes);
            return put(key, valueBytes);
        }
        return false;
    }
    
    @Override
    public Object get(Serializable key)
        throws RemoteException
    {
        return fileCache.get(key);
    }
    
    @Override
    public boolean remove(Serializable key)
        throws RemoteException
    {
        return fileCache.remove(key);
    }
    
}
