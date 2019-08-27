package com.mskim.i18l.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mskim.i18l.dao.KeyDao;
import com.mskim.i18l.dto.KeyDto;

@Service
public class I18nServiceImpl implements I18nService {

	@Autowired
	private KeyDao keyDao;
	
	@Override
	public void addKey(KeyDto key) {
		
		keyDao.insertKey(key);
		
	}

	@Override
	public List<HashMap<String,Object>> getKeys(String name) {
		
		Map<String,String> queryParamMap = new HashMap<>();
		queryParamMap.put("name", name);
		
		return keyDao.selectKeys(queryParamMap);
	}

}
