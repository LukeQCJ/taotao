package com.taotao.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * receive Activemq message
 * */
public class MyMessageListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		try{
			TextMessage textMessage = (TextMessage)message;
			String text = textMessage.getText();
			System.out.println("MyMessageListener consume: "+text);
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}
