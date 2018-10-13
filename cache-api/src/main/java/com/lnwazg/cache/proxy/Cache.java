package com.lnwazg.cache.proxy;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.lnwazg.cache.rmi.IRemoteCache;
import com.lnwazg.kit.compress.GzipBytesUtils;
import com.mchange.lang.ByteUtils;

/**
 * 是远程的IRemoteCache的一个代理<br>
 * 通过SerializationUtils.serialize(value) 以及 SerializationUtils.deserialize(bytes) 这两个方法，可以隐藏RMI交互过程中的类型信息<br>
 * 通过这种方法，可以完美解决服务端与客户端两端都要保存Class类型信息的问题<br>
 * 对于key，同样做了序列化保存。并且为了防止每次序列化后生成的byte[]的==问题，将其toHexAscii()<br>
 * 这样，即可完美解决key和value的序列化问题<br>
 * 增加实现了安全加密传输功能<br>
 * 安全传输仅能实现客户端自己的加解密，服务端无法做安全校验。因此只是掩耳盗铃而已！并且会增加cpu负载，降低并发量，因此没有存在的意义！因此可以去除掉！
 * @author lnwazg@126.com
 * @version 2016年10月16日
 */
public class Cache
{
    private IRemoteCache iRemoteCache;
    
    /**
     * 业务编码
     */
    private String bid;
    
    /**
     * 远程cache对象的一个代理
     * @param remoteCache
     */
    public Cache(IRemoteCache remoteCache)
    {
        iRemoteCache = remoteCache;
    }
    
    public Cache(IRemoteCache remoteCache, String bid)
    {
        this.iRemoteCache = remoteCache;
        this.bid = bid;
    }
    
    /**
     * 获取传输给redis服务器的key
     * @author nan.li
     * @param keyBytes
     * @return
     */
    private Serializable getKey(Serializable key)
    {
        //1.序列化
        byte[] keyBytes = SerializationUtils.serialize(key);
        //2.压缩
        keyBytes = GzipBytesUtils.zip(keyBytes);
        //ByteUtils.toHexAscii这样就可以完美区分出key了
        //因为直接传byte[]的话，每次序列化后的byte[]是不相等的，所以会有问题。所以最终还是要序列化成hexAscii才比较好
        //3.bid绑定
        return String.format("%s%s", (StringUtils.isEmpty(bid) ? "" : bid), ByteUtils.toHexAscii(keyBytes));
    }
    
    /**
     * 存
     * @author nan.li
     * @param key
     * @param value
     */
    public void put(Serializable key, Serializable value)
    {
        try
        {
            byte[] valueBytes = SerializationUtils.serialize(value);
            valueBytes = GzipBytesUtils.zip(valueBytes);
            iRemoteCache.put(getKey(key), valueBytes);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 对某个key的值+1
     * @author nan.li
     * @param key
     * @return
     */
    public boolean incr(Serializable key)
    {
        try
        {
            return iRemoteCache.incr(getKey(key));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 对某个key的值-1
     * @author nan.li
     * @param key
     * @return
     */
    public boolean decr(Serializable key)
    {
        try
        {
            return iRemoteCache.decr(getKey(key));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 移除某个key
     * @author nan.li
     * @param key
     * @return
     */
    public boolean remove(Serializable key)
    {
        try
        {
            return iRemoteCache.remove(getKey(key));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 根据key从远程缓存服务器中获取到对应的value对象<br>
     * key和value必须都要实现序列化接口
     * @author lnwazg@126.com
     * @param key
     * @return
     * @throws RemoteException
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Serializable key, Class<T> classType)
    {
        return (T)get(key);
    }
    
    /**
     * 根据key从远程缓存服务器中获取到对应的value对象<br>
     * key和value必须都要实现序列化接口
     * @author nan.li
     * @param key
     * @return
     */
    public Object get(Serializable key)
    {
        byte[] valueBytes;
        try
        {
            valueBytes = (byte[])iRemoteCache.get(getKey(key));
            if (valueBytes != null)
            {
                valueBytes = GzipBytesUtils.unzip(valueBytes);
            }
            if (valueBytes != null)
            {
                return SerializationUtils.deserialize(valueBytes);
            }
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
