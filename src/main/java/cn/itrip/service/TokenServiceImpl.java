package cn.itrip.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisAPI;
import com.alibaba.fastjson.JSONArray;
import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import cz.mallat.uasparser.UserAgentInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service("tokenService")
public class TokenServiceImpl implements TokenService {

    @Resource
    RedisAPI redisAPI;

    @Override
    public String createToken(String userAgent, ItripUser user) {
        StringBuffer sf=new StringBuffer("token:");
        //初始化浏览器解析工具类
        UASparser uaSparser = null;
        try {
            uaSparser = new UASparser(OnlineUpdater.getVendoredInputStream());
            //浏览器信息类，检测客户端类型，因为要根据客户端类型生成不同的Token
            UserAgentInfo userAgentInfo = uaSparser.parse(userAgent);

            System.out.println(userAgentInfo.getDeviceType()+"============================");
            if ("Personal computer".equals(userAgentInfo.getDeviceType())) {
            //如果是pc
                sf.append("PC-");
            } else {
                sf.append("MOBILE-");
            }
            //在追加userCode
            sf.append(MD5.getMd5(user.getUserCode(), 32) + "-");
            //加id
            sf.append(user.getId() + "-");
            //添加当前系统时间
            sf.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-");
            //追加随机6
            sf.append(MD5.getMd5(userAgent, 6));

            return sf.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(String token, ItripUser user) {
       // redisAPI = new RedisAPI();
        if (token.startsWith("tocken:PC")){
            redisAPI.set(token,2*60*60, JSONArray.toJSONString(user));
        }else {
            redisAPI.set(token,JSONArray.toJSONString(user));
        }
    }
}
