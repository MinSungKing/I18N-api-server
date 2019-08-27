package com.mskim.i18l.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mskim.i18l.dto.KeyDto;

public interface KeyDao {
	
	void insertKey(KeyDto key);
	List<HashMap<String,Object>> selectKeys(Map<String,String> param);

}
