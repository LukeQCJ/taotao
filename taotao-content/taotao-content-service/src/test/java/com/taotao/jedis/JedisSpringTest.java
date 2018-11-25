package com.taotao.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JedisSpringTest {

	@Test
	@SuppressWarnings("resource")
	public void testJedisClientPool(){
		//初始化spring容器
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		//从容器中获取JedisClient对象
		JedisClient jedisClient = ac.getBean(JedisClient.class);
		//使用JedisClient对象操作redis
		jedisClient.set("hello_key", "123456");
		String hello = jedisClient.get("hello_key");
		System.out.println(hello);
	}
}
