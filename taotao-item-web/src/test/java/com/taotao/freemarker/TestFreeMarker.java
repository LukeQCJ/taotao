package com.taotao.freemarker;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TestFreeMarker {

	@Test
	public void testFreeMarker() throws Exception {
		//1.create a template file
		//2.create a configuration object
		Configuration configuration = new Configuration(Configuration.getVersion());
		//3.set template path
		configuration.setDirectoryForTemplateLoading(new File("D:/javaLearning/workSpaceTaoTao/taotao-item-web/src/main/webapp/WEB-INF/ftl/"));
		//4.set template charset,usually utf-8
		configuration.setDefaultEncoding("utf-8");
		//5.use configuration object to load a template file,there should be a template name.
		//Template template = configuration.getTemplate("hello.ftl");
		Template template = configuration.getTemplate("student.ftl");
		//6.create a data set,pojo or map,and map is recommended
		Map data = new HashMap<>();
		data.put("hello", "Hello FreeMarker!");
		Student s = new Student(1,"张春生",23,"北京");
		data.put("student", s);
		List<Student> sl = new ArrayList<>();
		sl.add(s);
		sl.add(s);
		sl.add(s);
		data.put("stuList", sl);
		data.put("date", new Date());
		//7.create a writer object,and set a output file name and path
		Writer out = new FileWriter(new File("D:/javaLearning/ftl/student.html"));
		//8.use the method "process" template object to output file
		//template.process(data, out);
		template.process(data, out);
		//9.close stream
		out.close();
	}
}
