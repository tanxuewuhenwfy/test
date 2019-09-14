package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.userinfo.ItripUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import cn.itrip.common.MD5;
import cn.itrip.service.UserService;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    @Resource
    private UserService userService;

    //使用邮箱激活
    @RequestMapping(value="/activate",method=RequestMethod.POST,produces= "application/json")
    public  Dto activate(@RequestParam String user, @RequestParam String code){
        try {
            if(userService.activateMail(user, code))
            {
                return DtoUtil.returnSuccess("激活成功");
            }else{
                return DtoUtil.returnSuccess("激活失败");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return DtoUtil.returnFail("激活失败", ErrorCode.AUTH_ACTIVATE_FAILED);
        }
    }

    //使用邮箱注册
    @RequestMapping(value = "/doregister", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public Dto registByMail(@RequestBody ItripUserVO itripUserVO) {
        if (!validEmail(itripUserVO.getUserCode())) {
            return DtoUtil.returnFail("邮箱格式不正确", ErrorCode.AUTH_ILLEGAL_USERCODE);
        }
        try {
            if (userService.findItripUserByCode(itripUserVO.getUserCode()) == null) {
                //实例化对象
                ItripUser itripUser = new ItripUser();
                itripUser.setUserCode(itripUserVO.getUserCode());
                itripUser.setUserName(itripUserVO.getUserName());
                //加密
                itripUser.setUserPassword(MD5.getMd5(itripUserVO.getUserPassword(), 32));
                //注册用户
                userService.itripCreateUserByMail(itripUser);
                return DtoUtil.returnSuccess();
            }else
            {
                return DtoUtil.returnFail("用户已存在，注册失败", ErrorCode.AUTH_USER_ALREADY_EXISTS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
        }
    }

    /**
     * 使用手机注册
     *
     * @param itripUserVO
     * @return
     */
    @RequestMapping(value = "/registByPhone", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public Dto registByPhoneNumber(@RequestBody ItripUserVO itripUserVO) {
        System.out.println(itripUserVO);
        //验证手机号正确性
        if (!validPhone(itripUserVO.getUserCode())) {
            return DtoUtil.returnFail("手机号码格式不正确!", ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //判断用户是否存在
        ItripUser user = null;
        try {
            user = userService.findItripUserByCode(itripUserVO.getUserCode());
            if (user != null) {
                return DtoUtil.returnFail("用户已存在", ErrorCode.AUTH_USER_ALREADY_EXISTS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("对象查找失败!", ErrorCode.AUTH_UNKNOWN);
        }

        //实例化user对象
        ItripUser itripUser = new ItripUser();
        itripUser.setUserCode(itripUserVO.getUserCode());
        itripUser.setUserName(itripUserVO.getUserName());
        //密码加密
        itripUser.setUserPassword(MD5.getMd5(itripUserVO.getUserPassword(), 32));
        //注册用户信息
        try {
            userService.IteripCreateUserByPhone(itripUser);
            return DtoUtil.returnSuccess("注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("注册失败!", ErrorCode.AUTH_UNKNOWN);
        }
    }

    /**
     * 手机短信码验证
     */
    @RequestMapping(value = "/validatePhone")
    public Dto validatePhone(String userCode, String validateCode) {
        //调用验证方法
        try {
            if (userService.validatePhoneCode(userCode, validateCode)) {
                return DtoUtil.returnSuccess("验证成功!");
            } else {
                return DtoUtil.returnFail("验证码错误!", ErrorCode.AUTH_ILLEGAL_USERCODE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("验证失败!", ErrorCode.AUTH_ILLEGAL_USERCODE);
        }

    }

    /**
     * 验证是否合法的手机号
     *
     * @param phone
     * @return
     */
    private boolean validPhone(String phone) {
        String regex = "^1[3578]{1}\\d{9}$";
        return Pattern.compile(regex).matcher(phone).find();
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return
     */
    private boolean validEmail(String email) {
        String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        return Pattern.compile(regex).matcher(email).find();
    }

}
