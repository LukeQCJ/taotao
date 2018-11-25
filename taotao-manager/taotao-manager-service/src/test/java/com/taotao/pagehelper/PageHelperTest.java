package com.taotao.pagehelper;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;
//import com.taotao.pojo.TbItemExample.Criteria;

public class PageHelperTest {

	@SuppressWarnings("resource")
	@Test
	public void testPageHelper(){
		//1.在mybatis的配置文件配置pagehelper分页插件
		
		//2.在执行查询之前，使用pagehelper的静态方法
		PageHelper.startPage(1, 20);
		//3.执行查询
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		TbItemMapper tbItemMapper = (TbItemMapper)ac.getBean(TbItemMapper.class);
		//创建example对象
		TbItemExample example = new TbItemExample();
//		Criteria c = example.createCriteria();
		List<TbItem> list = tbItemMapper.selectByExample(example);
		//4.取分页信息，使用pageInfo对象
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		System.out.println("总计录数："+pageInfo.getTotal());
		System.out.println("总计页数："+pageInfo.getPages());
		System.out.println("返回记录数："+list.size());
	}
}
