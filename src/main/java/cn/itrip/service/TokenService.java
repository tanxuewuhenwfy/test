package cn.itrip.service;

import cn.itrip.exception.TokenValidationFailedException;
import cn.itrip.beans.pojo.ItripUser;
import org.springframework.stereotype.Service;

/**
 * Token管理接口
 * @author hduser
 *
 */
public interface TokenService {

	public String createToken(String userAgent, ItripUser user);
	public void save(String token, ItripUser user);
}
