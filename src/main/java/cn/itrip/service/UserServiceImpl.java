package cn.itrip.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisAPI;
import cn.itrip.dao.ItripUserMapper;
import cn.itrip.exception.UserLoginFailedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {
    //注入itripUserMapper
    @Resource(name = "itripUserMapper")
    private ItripUserMapper itripUserMapper;
    @Resource
    private SmsService smsService;
    @Resource
    RedisAPI redisAPI;
    @Resource
    MailService mailService;

    /**
     * 激活邮箱
     * @param email
     * @param code
     * @return
     * @throws Exception
     */
    @Override
    public boolean activateMail(String email, String code) throws Exception {
        String mailKey = "activation: "+email;
        if (redisAPI.exist(mailKey)){
            if (redisAPI.get(mailKey).equals(code)){
                ItripUser user = this.findItripUserByCode(email);
                if (user !=null){
                    user.setActivated(1);
                    user.setUserType(0);
                    user.setFlatID(user.getId());
                    itripUserMapper.updateByPrimaryKey(user);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void itripCreateUserByMail(ItripUser user) throws Exception {
        //发送激活邮件
        String activationCode = MD5.getMd5(new Date().toLocaleString(), 32);
        mailService.sendActivationMail(user.getUserCode(), activationCode);
        //保存用户信息
        itripUserMapper.insert(user);
    }

    @Override
    public void IteripCreateUserByPhone(ItripUser itripUser) throws Exception {
        //调用添加方法
        int insert = itripUserMapper.insert(itripUser);
        //生成验证码
        String code= MD5.getRandomCode()+"";
        //发送短信 2-->代表2分钟
        smsService.send(itripUser.getUserCode(),"1",new String[]{code,"2"});
        //保存验证码到Redis中
        redisAPI.set("activation:"+itripUser.getUserCode(),120,code);

    }

    /**
     * 查询是否存在用户
     * @param userCode
     * @return
     * @throws Exception
     */
    @Override
    public ItripUser findItripUserByCode(String userCode) throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("userCode",userCode);

        List<ItripUser> list = itripUserMapper.getItripUserListByMap(map);
        if (list.size()>0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 校验用户
     * @param phoneNum  : 手机号
     * @param validateCode  : 验证码
     * @return
     * @throws Exception
     */
    @Override
    public boolean validatePhoneCode(String phoneNum, String validateCode) throws Exception {
        //使用phoneNum到Redis中查询验证码
        String code = redisAPI.get("activation:" + phoneNum);
        //redis中的和validateCode对比
        if(!(code!=null && code.equals(validateCode))){
            return false;
        }
        //先通过当前code获取一下当前对象----如果对象不存在,就没有激活的概念
        ItripUser itripUser=this.findItripUserByCode(phoneNum);
        if (itripUser!=null){
            //修改属性
            itripUser.setUserType(0);
            itripUser.setFlatID(1L);//登录平台类型
            itripUser.setActivated(1);
            //调用修改方法
            itripUserMapper.updateByPrimaryKey(itripUser);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public ItripUser login(String name, String password) throws Exception {
        //封装参数的
        Map<String, Object> map = new HashMap<>();
        map.put("userCode", name);
        List<ItripUser> itripUsers = itripUserMapper.getItripUserListByMap(map);
        if (itripUsers.size()!=0){
            ItripUser user = itripUsers.get(0);
            if (user.getUserPassword().equals(password) && user.getActivated()==1){
                return user;
            }else {
                throw new UserLoginFailedException("用户未激活");
            }
        }
        return null;
    }
}
