package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.jedis.JedisClient;
import com.taotao.sso.service.UserService;

/**
 * user processing service
 * */
@Service
public class UserServiceImpl implements UserService {

	@Autowired 
	private TbUserMapper userMapper;
	
	@Autowired 
	private JedisClient jedisClient;
	
	@Value("${USER_SESSION}")
	private String USER_SESSION;
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	
	@Override
	public TaotaoResult checkData(String data, int type) {
		TbUserExample example = new TbUserExample();
		Criteria c = example.createCriteria();
		//user name is available or not
		if(type==1){
			c.andUsernameEqualTo(data);
		//phone number is available or not
		}else if(type==2){
			c.andPhoneEqualTo(data);
		//email is available or not
		}else if(type==3){
			c.andEmailEqualTo(data);
		}else{
			return TaotaoResult.build(400, "参数中包含非法数据！");
		}
		List<TbUser> list = userMapper.selectByExample(example);
		if(list != null && list.size() > 0){
			return TaotaoResult.ok(false);
		}
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult register(TbUser user) {
		//check data validity
		if(StringUtils.isBlank(user.getUsername())){
			return TaotaoResult.build(400, "user name should not be allowed empty!");
		}
		TaotaoResult taotaoResult = checkData(user.getUsername(), 1);
		if(!(boolean)taotaoResult.getData()){
			return TaotaoResult.build(400, "user name is used!");
		}
		if(StringUtils.isBlank(user.getPassword())){
			return TaotaoResult.build(400, "user password should not be allowed empty!");
		}
		if(StringUtils.isNoneBlank(user.getPhone())){
			taotaoResult = checkData(user.getPhone(), 2);
			if(!(boolean)taotaoResult.getData()){
				return TaotaoResult.build(400, "user phone is used!");
			}
		}
		if(StringUtils.isNoneBlank(user.getEmail())){
			taotaoResult = checkData(user.getEmail(), 3);
			if(!(boolean)taotaoResult.getData()){
				return TaotaoResult.build(400, "user email is used!");
			}
		}
		//
		user.setCreated(new Date());
		user.setUpdated(new Date());
		//password md5 encode
		String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5Password);
		
		userMapper.insert(user);
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult login(String userName, String password) {
		TbUserExample example = new TbUserExample();
		Criteria c = example.createCriteria();
		c.andUsernameEqualTo(userName);
		List<TbUser> list = userMapper.selectByExample(example);
		if(list == null || list.size() == 0){
			//return login failed
			return TaotaoResult.build(400, "login failed");
		}
		TbUser user = list.get(0);
		if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())){
			return TaotaoResult.build(400, "userName or password is not correct!");
		}
		user.setPassword(null);
		String token = UUID.randomUUID().toString();
		jedisClient.set(USER_SESSION+":"+token, JsonUtils.objectToJson(user));
		jedisClient.expire(USER_SESSION+":"+token, SESSION_EXPIRE);
		return TaotaoResult.ok(token);
	}

	@Override
	public TaotaoResult getUserByToken(String token) {
		String userJson = jedisClient.get(USER_SESSION+":"+token);
		if(StringUtils.isBlank(userJson)){
			return TaotaoResult.build(400, "login expired");
		}
		TbUser user = JsonUtils.jsonToPojo(userJson, TbUser.class);
		//reset session expire time
		jedisClient.expire(USER_SESSION+":"+token, SESSION_EXPIRE);
		return TaotaoResult.ok(user);
	}

	@Override
	public TaotaoResult logout(String token) {
		jedisClient.expire(USER_SESSION+":"+token,0);
		return TaotaoResult.ok();
	}

}
