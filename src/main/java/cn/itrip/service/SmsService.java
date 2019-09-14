package cn.itrip.service;

/**
 * 短信发送和邮件发送的业务类
 */
public interface SmsService {
    //手机短信发送
    public void send(String to, String tempId, String[] datas) throws Exception;
}
