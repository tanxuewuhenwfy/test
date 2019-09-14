package cn.itrip.service;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SmsServiceImpl implements SmsService {

    /**
     *
     * @param to 发送到哪里
     * @param tempId 模板id 测试阶段id= 1
     * @param datas 给模板填入的发送的值
     * @throws Exception
     */
    @Override
    public void send(String to, String tempId, String[] datas) throws Exception {
        //创建短信发送类 对象
        CCPRestSmsSDK ccpRestSmsSDK = new CCPRestSmsSDK();
        //设置IP地址
        ccpRestSmsSDK.init("app.cloopen.com","8883");
        //设置apiID
        ccpRestSmsSDK.setAppId("8a216da86bdeae7f016bef0f61ae09cc");
        //设置当前账号信息
        ccpRestSmsSDK.setAccount("8a216da86bdeae7f016bef0f615009c5","496cbe188bbf4637be884929d246680e");

        //发送
        HashMap<String, Object> map = ccpRestSmsSDK.sendTemplateSMS(to, tempId, datas);

        if(map.get("statusCode").equals("000000")){
            System.out.println("短信发送成功!");
        }else{
            throw  new Exception(map.get("statusCode")+"错误为:"+map.get("statusMsg"));
        }

    }
}
