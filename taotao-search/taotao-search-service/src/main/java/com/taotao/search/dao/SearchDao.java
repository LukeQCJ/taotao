package com.taotao.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;

@Repository
public class SearchDao {

	@Autowired
	private SolrServer solrServer;
	
	public SearchResult search(SolrQuery query) throws Exception {
		//execute query according to query object
		QueryResponse response = solrServer.query(query);
		//get search results
		SolrDocumentList solrDocumentList = response.getResults();
		//get search results total rows
		long numFound = solrDocumentList.getNumFound();
		SearchResult result = new SearchResult();
		result.setRecordCount(numFound);
		List<SearchItem> itemList = new ArrayList();
		//encapulate search results into SearchItem object
		for(SolrDocument d : solrDocumentList){
			SearchItem item = new SearchItem();
			item.setCategory_name((String)d.get("item_category_name"));
			item.setId((String)d.get("id"));
			//just use one image to display in the search page
			String image = (String)d.get("item_image");
			if(StringUtils.isNotBlank(image)){
				image = image.split(",")[0];
			}
			item.setImage(image);
			item.setPrice((long)d.get("item_price"));
			item.setSell_point((String)d.get("item_sell_point"));
			//get highlighting 
			Map<String,Map<String,List<String>>> highlighting = response.getHighlighting();
			List<String> list = highlighting.get(d.get("id")).get("item_title");
			String itemTitle = "";
			if(list != null && list.size() > 0){
				itemTitle = list.get(0);
			}else {
				itemTitle = (String)d.get("item_title");
			}
			item.setTitle((String)d.get("item_title"));
			//add to item list
			itemList.add(item);
		}
		//add result into SearchResult 
		result.setItemList(itemList);
		//return
		return result;
	}
}
