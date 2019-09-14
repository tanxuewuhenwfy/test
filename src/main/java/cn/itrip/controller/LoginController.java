package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import cn.itrip.common.MD5;
import cn.itrip.service.TokenService;
import cn.itrip.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;


@RestController
@RequestMapping(value = "/api")
public class LoginController {

    @Resource
    private UserService userService;

    @Resource
    private TokenService tokenService;

    @RequestMapping("/dologin")
    public Dto login(String name, String password, HttpServletRequest request) {
        System.out.println(name+"========="+password);
        try {
            ItripUser user = userService.login(name, MD5.getMd5(password, 32));
            if (user!=null){
                //获取请求头
                String userAgent = request.getHeader("user-Agent");
                //生成tocken
                String token = tokenService.createToken(userAgent, user);
                //保存tocken到redis中
                tokenService.save(token,user);

                //把当前的token的所有信息封装到ItripTokenVO中
                ItripTokenVO itripTokenVO=new ItripTokenVO(token, Calendar.getInstance().getTimeInMillis()+2*60*60,
                        Calendar.getInstance().getTimeInMillis());

                return  DtoUtil.returnDataSuccess(itripTokenVO);
            }else{
                return DtoUtil.returnFail("对象不存在!", ErrorCode.AUTH_ILLEGAL_USERCODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.AUTH_REPLACEMENT_FAILED);
        }
    }
}
