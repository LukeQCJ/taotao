package com.taotao.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;

@Controller
public class CartController {

	@Value("${TT_CART}")
	private String TT_CART;
	
	@Value("${CookieMaxAge}")
	private Integer cookieMaxAge;
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/cart/add/${itemId}")
	public  String addItemCart(@PathVariable Long itemId,
			@RequestParam(defaultValue="1")Integer num , 
			HttpServletRequest request, HttpServletResponse response){
		List<TbItem> cartItemList = getCardItemList(request);
		boolean flag = false;
		for(TbItem tbItem:cartItemList){
			if(tbItem.getId() == itemId.longValue()){
				tbItem.setNum(tbItem.getNum() + num);
				flag = true;
				break;
			}
		}
		if(!flag){
			TbItem tbItem = itemService.getItemById(itemId);
			tbItem.setNum(num);
			String image = tbItem.getImage();
			if(StringUtils.isNoneBlank(image)){
				String[] images = image.split(",");
				tbItem.setImage(images[0]);
			}
			cartItemList.add(tbItem);
		}
		CookieUtils.setCookie(request, response, TT_CART,
				JsonUtils.objectToJson(cartItemList),cookieMaxAge,true);
		return "cartSuccess";
	}
	
	private List<TbItem> getCardItemList(HttpServletRequest request){
		String json = CookieUtils.getCookieValue(request, TT_CART,true);
		if(StringUtils.isBlank(json)){
			return new ArrayList<TbItem>();
		}
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request){
		List<TbItem> list = getCardItemList(request);
		request.setAttribute("cartList", list);
		return "cart";
	}
	
	@RequestMapping("/cart/update/{itemId}/{num}")
	@ResponseBody
	public TaotaoResult updateItemNum(@PathVariable Long itemId, @PathVariable Integer num,
			HttpServletRequest request, HttpServletResponse response){
		List<TbItem> list = getCardItemList(request);
		for(TbItem tbItem:list){
			if(tbItem.getId() == itemId.longValue()){
				tbItem.setNum(num);
				break;
			}
		}
		CookieUtils.setCookie(request, response, TT_CART,
				JsonUtils.objectToJson(list),cookieMaxAge,true);
		return TaotaoResult.ok();
	}
	
	@RequestMapping()
	public String deleteCartItem(@PathVariable Long itemId,
			HttpServletRequest request, HttpServletResponse response){
		List<TbItem> list = getCardItemList(request);
		for(TbItem tbItem :list){
			if(tbItem.getId() == itemId.longValue()){
				list.remove(tbItem);
				break;
			}
		}
		CookieUtils.setCookie(request, response, TT_CART,
				JsonUtils.objectToJson(list),cookieMaxAge,true);
		return "redirect:/cart/cart.html";
	}
}
