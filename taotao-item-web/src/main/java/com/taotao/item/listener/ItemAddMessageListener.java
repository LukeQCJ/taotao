package com.taotao.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

import freemarker.template.Configuration;
import freemarker.template.Template;


public class ItemAddMessageListener implements MessageListener {

	@Autowired
	private ItemService itemService;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@Value("${HTML_OUT_PATH}")
	private String HTML_OUT_PATH;
	
	@Override
	public void onMessage(Message message) {
		try{
			//get item id from the message
			TextMessage textMessage = (TextMessage)message;
			String strId = textMessage.getText();
			Long itemId = Long.parseLong(strId);
			//wait the add item transaction commit
			Thread.sleep(1000);
			//query item info and description according to the item id
			TbItem tbItem = itemService.getItemById(itemId);
			Item item = new Item(tbItem);
			TbItemDesc tbItemDesc = itemService.getItemDescById(itemId);
			//use freemarker to generate static page 
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			//1.create template 
			//2.load template object
			Template template = configuration.getTemplate("item.ftl");
			//3.prepare data for template
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("item", item);
			data.put("itemDesc", tbItemDesc);
			//4.set output directory and file name
			Writer out = new FileWriter(new File(HTML_OUT_PATH + strId + ".html"));
			//5.generate static page
			template.process(data, out);
			//6.close file stream
			out.close();
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}
