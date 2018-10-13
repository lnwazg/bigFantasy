package com.lnwazg.cache.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 远程缓存接口<br> 
 * 定义一个远程接口，必须继承Remote接口，其中需要远程调用的方法必须抛出RemoteException异常 
 */
public interface IRemoteCache extends Remote
{
    /**
     * 将一个键值对放入到远程缓存服务器中<br>
     * key和value必须都要实现序列化接口
     * @author lnwazg@126.com
     * @param key
     * @param value
     * @return
     * @throws RemoteException
     */
    public boolean put(Serializable key, Serializable value)
        throws RemoteException;
        
    /**
     * 将 key 中储存的数字值增一<br>
     * 若key不存在，或value包含错误的类型（非Integer），则不做任何操作，并返回false<br>
     * 该操作为原子操作
     * @author nan.li
     * @param key
     * @throws RemoteException
     */
    public boolean incr(Serializable key)
        throws RemoteException;
        
    /**
     * 将 key 中储存的数字值减一<br>
     * 若key不存在，或value包含错误的类型（非Integer），则不做任何操作，并返回false<br>
     * 该操作为原子操作
     * @author lnwazg@126.com
     * @param key
     * @return
     * @throws RemoteException
     */
    public boolean decr(Serializable key)
        throws RemoteException;
        
    /**
     * 根据key从远程缓存服务器中获取到对应的value对象<br>
     * key和value必须都要实现序列化接口
     * @author lnwazg@126.com
     * @param key
     * @return
     * @throws RemoteException
     */
    public Object get(Serializable key)
        throws RemoteException;
        
    /**
     * 删除掉某个key
     * @author nan.li
     * @param key
     * @return
     * @throws RemoteException
     */
    public boolean remove(Serializable key)
        throws RemoteException;
        
}