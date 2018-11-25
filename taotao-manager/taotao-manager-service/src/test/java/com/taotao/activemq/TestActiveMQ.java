package com.taotao.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

import javax.jms.Queue;

public class TestActiveMQ {

	//queue
	//producer
	@Test
	public void testQueueProducer() throws Exception {
		//1.create a connection factory object, need to MQ ip:port
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.190.128:61616");
		//2.use connecttion factory to create a connection
		Connection c = cf.createConnection();
		//3.start connection
		c.start();
		//4.use connection to create a session object
		//the first param is transaction flag, usually use false to insure the data final consistency.
		//if the first param is true, the second param will be ignored,otherwise, the second param is message response mode.
		Session session = c.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
		//5.use session to create destination objec,queue or topic
		Queue queue = session.createQueue("test-queue");
		//6.use session to create a producer object
		MessageProducer producer = session.createProducer(queue);
		//7.create a TextMessage object
		//TextMessage textMessage = session.createTextMessage("Hello ActiveMQ!");
		TextMessage textMessage = new ActiveMQTextMessage();
		textMessage.setText("Hello ActiveMQ!");
		//8.send message
		producer.send(textMessage);
		//9.release resources
		producer.close();
		session.close();
		c.close();
	}
	
	@Test
	public void testQueueConsumer() throws Exception {
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.190.128:61616");
		
		Connection c = cf.createConnection();
		
		c.start();
		
		Session session = c.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
		
		Queue queue = session.createQueue("test-queue");
		
		MessageConsumer consumer = session.createConsumer(queue);
		
		consumer.setMessageListener(new MessageListener(){

			@Override
			public void onMessage(Message message) {
				if(message instanceof TextMessage){
					TextMessage tm = (TextMessage)message;
					System.out.println("consume message: "+tm);
				}
				
			}
			
		});
		//system sleep in order to get messages
		/*while(true){
			Thread.sleep(1000);
		}*/
		System.in.read();
		
		consumer.close();
		session.close();
		c.close();
	}
	
	@Test
	public void testTopicProducer() throws Exception {
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.190.128:61616");
		
		Connection c = cf.createConnection();
		
		c.start();
		
		Session session = c.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
		
		Topic topic = session.createTopic("test-topic");
		
		MessageProducer producer = session.createProducer(topic);
		
		TextMessage textMessage = session.createTextMessage("Hello ActiveMQ Topic!");
		
		producer.send(textMessage);
		
		producer.close();
		session.close();
		c.close();
	}
	
	@Test
	public void testTopicConsumer() throws Exception {
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.190.128:61616");
		
		Connection c = cf.createConnection();
		
		c.start();
		
		Session session = c.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
		
		Topic topic = session.createTopic("test-topic");
		
		MessageConsumer consumer = session.createConsumer(topic);
		
		consumer.setMessageListener(new MessageListener(){

			@Override
			public void onMessage(Message message) {
				if(message instanceof TextMessage){
					TextMessage tm = (TextMessage)message;
					System.out.println("consume topic message: "+tm);
				}
				
			}
			
		});
		System.out.println("consumer3");
		//system sleep in order to get messages
		/*while(true){
			Thread.sleep(1000);
		}*/
		System.in.read();
		
		consumer.close();
		session.close();
		c.close();
	}
}
