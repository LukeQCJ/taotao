package com.taotao.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;

/**
 * user processing controller
 * 
 * */
@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@Value("${TT_TOKEN}")
	private String TT_TOKEN;
	
	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public TaotaoResult checkUserData(@PathVariable String param,
			@PathVariable Integer type){
		TaotaoResult result = userService.checkData(param, type);
		return result;
	}
	
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult register(TbUser user){
		TaotaoResult result = userService.register(user);
		return result;
	}
	
	@RequestMapping(value="/user/lgoin",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult login(String userName, String password,
			HttpServletRequest request, HttpServletResponse response){
		TaotaoResult result = userService.login(userName,password);
		//set token to cookie
		CookieUtils.setCookie(request, response, TT_TOKEN, result.getData().toString());
		return result;
	}
	
	/**
	 * need to spring 4.1+ version can support this resolution
	 * */
	/*@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET)
	@ResponseBody
	public Object getUserByToken(String token,String callback){
		TaotaoResult result = userService.getUserByToken(token);
		//if the request is JSONP type
		if(StringUtils.isNotBlank(callback)){
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			//set callback method
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
		return result;
	}*/
	
	@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET,
			//set response data content-type
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getUserByToken(String token,String callback){
		TaotaoResult result = userService.getUserByToken(token);
		//if the request is JSONP type
		if(StringUtils.isNotBlank(callback)){
			return callback + "(" + JsonUtils.objectToJson(result) + ");";
		}
		return JsonUtils.objectToJson(result);
	}
	
	@RequestMapping(value="/user/logout/{token}",method=RequestMethod.GET)
	@ResponseBody
	public TaotaoResult logout(String token){
		TaotaoResult result = userService.logout(token);
		return result;
	}
}
