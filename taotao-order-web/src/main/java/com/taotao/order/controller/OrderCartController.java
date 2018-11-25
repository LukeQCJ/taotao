package com.taotao.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;

@Controller
public class OrderCartController {
	
	@Value("${TT_CART}")
	private String TT_CART;
	
	@Autowired
	private OrderService orderService;
	
	//
	@RequestMapping("/order/order_cart")
	public String showOrderCart(HttpServletRequest request){
		//user must be login status
		//get user id
		TbUser user = (TbUser) request.getAttribute("user");
		System.out.println("user name : "+user.getUsername());
		//get address info according to user info, now we use static data
		//put address info into pages
		//get cart goods info in cookie and display order items in the order-cart page
		List<TbItem> cartList = getCardItemList(request);
		//return logic view
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
	
	private List<TbItem> getCardItemList(HttpServletRequest request){
		String json = CookieUtils.getCookieValue(request, TT_CART,true);
		if(StringUtils.isBlank(json)){
			return new ArrayList<TbItem>();
		}
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	
	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo,Model model){
		TaotaoResult taotaoResult = orderService.createOrder(orderInfo);
		model.addAttribute("orderId", taotaoResult.getData().toString());
		model.addAttribute("payment", orderInfo.getPayment());
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusDays(3);
		model.addAttribute("date",dateTime.toString("yyyy-MM-dd"));
		return "success";
	}
	
}
