package com.mskim.i18l.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class KeyDto {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private int id;
	
	private String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}