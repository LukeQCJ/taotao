package com.taotao.activemq;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class SpringActivemq {

	@Test
	public void testJmsTemplate(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		JmsTemplate jmsTemplate = ac.getBean(JmsTemplate.class);
		Destination destination = (Destination) ac.getBean("test-queue");
		jmsTemplate.send(destination,new MessageCreator(){

			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage("spirng activemq send queue...");
				return message;
			}
			
		});
	}
}
