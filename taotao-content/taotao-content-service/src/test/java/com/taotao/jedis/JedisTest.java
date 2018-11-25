package com.taotao.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {

	@Test
	public void testJedis(){
		//需要创建一个jedis对象，包括制定服务的IP和port
		Jedis jedis = new Jedis("192.168.190.128",7001);
		//直接操作数据库
		String hello = jedis.get("hello_key");
		System.out.println("hello_key:"+hello);
		//关闭连接
		jedis.close();
	}
	
	@Test
	public void testJedisPool() throws Exception{
		//创建一个redis数据库连接池对象（单例），需要指定IP和port
		JedisPool jedisPool = new JedisPool("192.168.190.128",7001);
		//从连接池中获取连接
		Jedis jedis = jedisPool.getResource();
		//使用Jedis操作数据库（方法级别使用）
		String hello = jedis.get("hello_key");
		System.out.println("hello_key:"+hello);
		//一定要关闭Jedis连接
		jedis.close();
		//系统关闭前关闭连接池
		jedisPool.close();
	}
	
	@Test
	public void testJedisCluster() throws Exception{
		//创建一个JedisCluster对象，构造参数Set类型，集合中每个元素是HostAndPort类型
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		//向集合中添加节点
		nodes.add(new HostAndPort("192.168.190.128",7001));
		nodes.add(new HostAndPort("192.168.190.128",7002));
		nodes.add(new HostAndPort("192.168.190.128",7003));
		nodes.add(new HostAndPort("192.168.190.128",7004));
		nodes.add(new HostAndPort("192.168.190.128",7005));
		nodes.add(new HostAndPort("192.168.190.128",7006));
		JedisCluster jedisCluster = new JedisCluster(nodes);
		//直接使用JedisCluster操作redis，自带连接池。jedisCluster对象可以是单例的。
		jedisCluster.set("hello_key2", "I am jedis cluster!");
		System.out.println(jedisCluster.get("hello_key2"));
		//系统关闭前关闭JedisCluster
		jedisCluster.close();
	}
}
