package com.mskim.i18l.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mskim.i18l.dto.KeyDto;

public interface KeyDao {
	
	int insertKey(KeyDto key);
	
	List<HashMap<String,Object>> selectKeys(Map<String,String> params);
	
	KeyDto selectKeyById(KeyDto key);
	
	KeyDto selectKeyByName(KeyDto key);
	
	int updateKey(KeyDto key);
	
}
