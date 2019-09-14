package cn.itrip.service;

import java.util.List;
import java.util.Set;

import cn.itrip.beans.pojo.ItripUser;

/**
 * 用户管理接口
 * @author hduser
 *
 */
public interface UserService {

	public ItripUser login(String name, String password) throws Exception;
	//手机注册
	public void  IteripCreateUserByPhone(ItripUser itripUser) throws Exception;
	//根据userCode判断当前用户是否已存在
	public ItripUser findItripUserByCode(String userCode) throws Exception;
	//手机短信验证
	public boolean  validatePhoneCode(String phoneNum,String validateCode) throws Exception;
	//邮箱注册
	public void itripCreateUserByMail(ItripUser user) throws Exception;
	//邮箱激活
	public boolean activateMail(String email,String code) throws Exception;
}
