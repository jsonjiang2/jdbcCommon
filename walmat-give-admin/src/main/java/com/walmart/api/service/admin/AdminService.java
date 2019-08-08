package com.walmart.api.service.admin;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.walmart.api.model.admin.Admin;
import com.walmart.common.dbsource.DBSource;
import com.walmart.common.process.ProcessBack;
import com.walmart.common.service.BaseService;

@Service
public class AdminService extends BaseService<Admin>{
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings("unchecked")
	public String login(Admin admin){
		String sql = "select * from admin WHERE name=? and pwd=?";
		Object[] params = new Object[]{admin.getName(),admin.getPwd()};
		Admin admin1 = (Admin)jdbcTemplate.queryForObject(sql, params,new RowMapper(){
			 @Override  
             public Object mapRow(ResultSet rs, int rowNum)throws SQLException {  
			 	Admin admin11 = new Admin();
			    admin11.setId(Long.valueOf(rs.getInt("id")));
			    admin11.setName(rs.getString("name"));
			    admin11.setPwd(rs.getString("pwd"));
		        return admin11;
			 }   
		});
		if(admin1!=null){
			return "登陆成功！";
		}
		return "账号/密码错误";
	}

	public ProcessBack save(Admin admin) {
		if(admin.getId()!=0 && admin.getId()!=null){
			return updateById(admin);
		}
		return insert(admin,DBSource.getMysql());
	}

}
