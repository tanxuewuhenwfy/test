package test;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.dao.ItripUserMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItripTest {

    @Autowired
    ItripUserMapper mapper;

    @Test
    public void fun(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        ItripUserMapper itripUserMapper = (ItripUserMapper) context.getBean("itripUserMapper");
        //封装参数的
        Map<String, Object> map = new HashMap<>();
        map.put("userCode", "1044732267@qq.com");
        try {
            ItripUser user = itripUserMapper.selectByPrimaryKey(12l);
            System.out.println(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
