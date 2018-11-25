package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;

public class LoginInterceptor implements HandlerInterceptor {

	@Value("${TT_TOKEN}")
	private String TT_TOKEN;
	
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	@Autowired
	private UserService userService;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		//after modelAndView

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		//after handler executed,before modelAndview

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//must execute this method before executing handler 
		//1. get token from cookie
		String token = CookieUtils.getCookieValue(request, TT_TOKEN);
		//2. if no token,go to sso login page,and need to set current page url as param to sso, after sso success, go back to this url
		if(StringUtils.isBlank(token)){
			String requestURL = request.getRequestURL().toString();
			response.sendRedirect(SSO_URL+"/page/login?url="+requestURL);
			return false;
		}
		//3. get token,check user is login status or not
		TaotaoResult taotaoResult = userService.getUserByToken(token);
		//4. if user did not login,go to sso login page
		if(taotaoResult.getStatus()!= 200){
			String requestURL = request.getRequestURL().toString();
			response.sendRedirect(SSO_URL+"/page/login?url="+requestURL);
			return false;
		}
		//5. if user is login status, allow request
		//set user info into request
		TbUser user = (TbUser) taotaoResult.getData();
		request.setAttribute("user", user);
		//return true: allow access, false: intercept
		return true;
	}

}
