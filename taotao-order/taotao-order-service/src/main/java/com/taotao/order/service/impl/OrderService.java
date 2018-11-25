package com.taotao.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.extension.Activate;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;

@Service
public class OrderService implements com.taotao.order.service.OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${ORDER_ID_GEN}")
	private String ORDER_ID_GEN;
	
	@Value("${ORDER_ID_INIT_VALUE}")
	private String ORDER_ID_INIT_VALUE;
	
	@Value("${ORDER_ITEM_ID_GEN}")
	private String ORDER_ITEM_ID_GEN;
	
	@Override
	public TaotaoResult createOrder(OrderInfo orderInfo) {
		//generate order id, can use redis incr method 
		if(!jedisClient.exists(ORDER_ID_GEN)){
			jedisClient.set(ORDER_ID_GEN, ORDER_ID_INIT_VALUE);
		}
		String orderId = jedisClient.incr(ORDER_ID_GEN).toString();
		//insert order info into order table
		orderInfo.setOrderId(orderId);
		orderInfo.setPostFee("0");
		orderInfo.setStatus(1);
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		orderMapper.insert(orderInfo);
		//insert order item info into orderitem table
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			String oid = jedisClient.incr(ORDER_ITEM_ID_GEN).toString();
			tbOrderItem.setId(oid);
			tbOrderItem.setOrderId(orderId);
			orderItemMapper.insert(tbOrderItem);
		}
		//insert order shipping info into ordershipping table
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		orderShippingMapper.insert(orderShipping);
		//return order id
		return TaotaoResult.ok(orderId);
	}

}
