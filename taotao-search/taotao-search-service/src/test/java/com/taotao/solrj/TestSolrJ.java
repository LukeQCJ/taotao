package com.taotao.solrj;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrJ {

	@Test
	public void testAddDocument() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://192.168.190.128:8082/solr/collection1");
		
		SolrInputDocument document = new SolrInputDocument();
		
		document.addField("id", "124");
		document.addField("item_title", "测试商品1");
		document.addField("item_price", 1000);
		
		solrServer.add(document);
		
		solrServer.commit();
	}
	
	@Test
	public void deleteDocumentById() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://192.168.190.128:8082/solr/collection1");
		solrServer.deleteById("test001");
		solrServer.commit();
	}
	
	@Test
	public void deleteByQuery() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://192.168.190.128:8082/solr/collection1");
		solrServer.deleteByQuery("id:123");
	}
	
	@SuppressWarnings("unused")
	@Test
	public void searchDocument() throws Exception {
		//create SolrServer object
		SolrServer solrServer = new HttpSolrServer("http://192.168.190.128:8082/solr/collection1");
		//create SolrQuery object
		SolrQuery query = new SolrQuery();
		//set query condition,filter,paging,sorting,highlighting
		//query.set("q","*:*");
		query.setQuery("手机");
		//paging
		query.setStart(0);
		query.setRows(10);
		
		//set default search field
		query.set("df", "item_keywords");
		//set highlighting
		query.setHighlight(true);
		//set highlighting field
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<div>");
		query.setHighlightSimplePost("</div>");
		//execute query,get a response object
		QueryResponse response = solrServer.query(query);
		//get query response
		SolrDocumentList solrDocumentList = response.getResults();
		//get query result total rows
		System.out.println("total rows: "+solrDocumentList.getNumFound());
		for(SolrDocument d: solrDocumentList){
			System.out.println(d.get("id"));
			//get highlighting 
			Map<String,Map<String,List<String>>> highlighting = response.getHighlighting();
			List<String> list = highlighting.get(d.get("id")).get("item_title");
			String itemTitle = "";
			if(list != null && list.size() > 0){
				itemTitle = list.get(0);
			}else {
				itemTitle = (String)d.get("item_title");
			}
			
			System.out.println(d.get("item_title"));
			System.out.println(d.get("item_sell_point"));
			System.out.println(d.get("item_price"));
			System.out.println(d.get("item_image"));
			System.out.println(d.get("item_category_name"));
			System.out.println("=================================");
		}
	}
}
