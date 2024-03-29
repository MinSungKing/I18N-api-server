package com.mskim.i18l.dto;

import javax.validation.constraints.NotEmpty;

public class KeyDto {
	
	
	private int id;
	
	@NotEmpty
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