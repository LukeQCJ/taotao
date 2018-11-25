package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;

/**
 * 内容分类管理Service
 * */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService{

	@Autowired
	private TbContentCategoryMapper tbContentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria c = example.createCriteria();
		c.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		List<EasyUITreeNode> resultList = new ArrayList<EasyUITreeNode>();
		for(TbContentCategory tcc : list){
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tcc.getId());
			node.setText(tcc.getName());
			node.setState(tcc.getIsParent()?"closed":"open");
			resultList.add(node);
		}
		return resultList;
	}

	@Override
	public TaotaoResult addContentCategory(Long parentId, String name) {
		TbContentCategory cc = new TbContentCategory();
		cc.setParentId(parentId);
		cc.setName(name);
		//状态，可选值：1（正常），2（删除）
		cc.setStatus(1);
		//排序，默认为1
		cc.setSortOrder(1);
		cc.setIsParent(false);
		cc.setCreated(new Date());
		cc.setUpdated(new Date());
		
		tbContentCategoryMapper.insert(cc);
		//判断父节点的状态
		TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()){
			//如果父节点为叶子节点应该改为父节点
			parent.setIsParent(true);
			//更新父节点
			tbContentCategoryMapper.updateByPrimaryKey(parent);
		}
		//返回结果
		return TaotaoResult.ok(cc);
	}

	@Override
	public TaotaoResult updateContentCategory(Long id,String name) {
		TbContentCategory cc = new TbContentCategory();
		cc.setId(id);
		cc.setName(name);
		int count = tbContentCategoryMapper.updateByPrimaryKeySelective(cc);
		TaotaoResult ttr = new TaotaoResult();
		if(count != 0){
			ttr.setStatus(0);
			ttr.setMsg("更新内容分类成功!");
			ttr.setData("");
		}else{
			ttr.setStatus(-1);
			ttr.setMsg("更新内容分类失败!");
			ttr.setData("");
		}
		return ttr;
	}

	@Override
	public TaotaoResult deleteContentCategory(Long id) {
		//判断对应的内容分类是否为父节点
		TbContentCategory tcc = tbContentCategoryMapper.selectByPrimaryKey(id);
		TaotaoResult ttr = new TaotaoResult();
		if(tcc == null){
			ttr.setStatus(-1);
			ttr.setMsg("删除的内容分类不存在！");
			ttr.setData("");
		}else if(tcc.getIsParent()){
			ttr.setStatus(-1);
			ttr.setMsg("不能删除内容分类父节点，需要先删除子节点！");
			ttr.setData("");
		}else{
			int count = tbContentCategoryMapper.deleteByPrimaryKey(id);
			if(count != 0){
				TbContentCategory cc = new TbContentCategory();
				cc.setId(tcc.getParentId());
				cc.setIsParent(false);
				tbContentCategoryMapper.updateByPrimaryKeySelective(cc);
				ttr.setStatus(0);
				ttr.setMsg("删除内容分类节点成功！");
				ttr.setData("");
			}else{
				ttr.setStatus(-1);
				ttr.setMsg("删除的内容分类失败！");
				ttr.setData("");
			}
		}
		return ttr;
	}

}
