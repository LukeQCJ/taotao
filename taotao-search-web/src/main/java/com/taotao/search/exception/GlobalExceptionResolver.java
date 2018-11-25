package com.taotao.search.exception;

import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 全局异常处理器
 * @since 2018/08/03
 * */
public class GlobalExceptionResolver implements HandlerExceptionResolver {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception e) {
		logger.info("============进入全局异常处理器=============");
		logger.debug("测试handler的类型"+handler.getClass().getName());
		// print exception in console
		e.printStackTrace();
		//write exception infos to log files
		logger.error("系统发生异常",e);
		//send email
		//jmail
		//send message
		//the third party support
		//show error to page
		ModelAndView mav = new ModelAndView();
		mav.addObject("message", e.getMessage());
		mav.setViewName("error/exception");
		return mav;
	}

}
