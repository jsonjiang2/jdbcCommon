package com.walmart.api.controller.admin;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.walmart.api.model.admin.Admin;
import com.walmart.api.service.admin.AdminService;
import com.walmart.common.process.ProcessBack;

@RestController
@RequestMapping("api/admin")
public class AdminController {
	
	@Autowired
	AdminService adminService;
	
    @RequestMapping(value="/login",method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String ceshi(Admin admin){
    	if(StringUtils.isAllEmpty(admin.getName())){
    		return "用户名为空";
    	}
    	if(StringUtils.isAllEmpty(admin.getPwd())){
    		return "密码为空";
    	}
    	
    	return adminService.login(admin);
    }
    
    @RequestMapping(value="/save",method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ProcessBack save(Admin admin){
    	return adminService.save(admin);
    }
    
    
}
