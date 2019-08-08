package com.walmart.api.service.userinfo;

import org.springframework.stereotype.Service;

import com.walmart.api.model.userinfo.UserInfo;
import com.walmart.common.dbsource.DBSource;
import com.walmart.common.process.ProcessBack;
import com.walmart.common.service.BaseService;

@Service
public class UserInfoService extends BaseService<UserInfo> {
	
	public ProcessBack save(UserInfo users) {
		return insert(users,DBSource.getMysql());
	}
}
