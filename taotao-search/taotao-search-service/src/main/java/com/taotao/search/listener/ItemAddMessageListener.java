package com.taotao.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.common.pojo.SearchItem;
import com.taotao.search.mapper.SearchItemMapper;

public class ItemAddMessageListener implements MessageListener {

	@Autowired
	private SearchItemMapper searchItemMapper;
	
	@Autowired 
	private SolrServer solrServer;
	
	@Override
	public void onMessage(Message message) {
		try{
			TextMessage textMessage = (TextMessage)message;
			String text = textMessage.getText();
			long itemId = Long.parseLong(text);
			//wait commit
			Thread.sleep(1000);
			SearchItem item = searchItemMapper.getItemById(itemId);
			
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", item.getId());
			document.addField("item_title", item.getTitle());
			document.addField("item_sell_point", item.getSell_point());
			document.addField("item_price", item.getPrice());
			document.addField("item_image", item.getImage());
			document.addField("item_category_name", item.getCategory_name());
			document.addField("item_desc", item.getItem_desc());
			solrServer.add(document);
			solrServer.commit();
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}
