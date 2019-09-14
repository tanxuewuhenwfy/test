package cn.itrip.service;

import cn.itrip.common.RedisAPI;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MailServiceImpl implements MailService {
    @Resource
    private MailSender mailSender;
    @Resource
    private RedisAPI redisAPI;
    @Resource
    private SimpleMailMessage activationMailMessage;

    @Override
    public void sendActivationMail(String mailTo, String activationCode) {
        activationMailMessage.setTo(mailTo);
        activationMailMessage.setText("注册邮箱: "+mailTo+" 激活码: "+activationCode);

        mailSender.send(activationMailMessage);

        redisAPI.set("activation: "+mailTo,30*60,activationCode);
    }
}
