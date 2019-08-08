package com.walmart.api.model.admin;

import com.walmart.common.annotation.CustomTableName;

@CustomTableName("admin")
public class Admin {
	
	private Long id;
	private String name;
	private String pwd;
	private Integer age;
	
	
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	
}
