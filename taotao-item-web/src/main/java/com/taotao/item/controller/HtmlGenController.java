package com.taotao.item.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * page staticalization processing controller
 * */
@Controller
public class HtmlGenController {

	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@RequestMapping("/genHtml")
	@ResponseBody
	public String genHtml() throws Exception {
		Configuration configuration = freeMarkerConfigurer.createConfiguration();
		Template template = configuration.getTemplate("hello.ftl");
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("hello", "Spring freemarker test!");
		Writer out = new FileWriter(new File("D:/javaLearning/ftl/test.html"));
		template.process(data, out);
		out.close();
		return "OK";
	}
}
