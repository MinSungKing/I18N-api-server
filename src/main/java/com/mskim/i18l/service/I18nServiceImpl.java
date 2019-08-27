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
	public KeyDto addKey(KeyDto key) {
		
		if(keyDao.insertKey(key) > 0) {
			KeyDto insertedKey = keyDao.selectKeyByName(key);
			return insertedKey;
		}
		else {
			return null;
		}
		
	}

	@Override
	public List<HashMap<String,Object>> getKeys(String name) {
		
		Map<String,String> queryParamMap = new HashMap<>();
		queryParamMap.put("name", name);
		
		return keyDao.selectKeys(queryParamMap);
	}

	@Override
	public KeyDto updateKey(KeyDto key) {

		if(keyDao.updateKey(key) > 0) {
			KeyDto updatedKey = keyDao.selectKeyById(key);
			return updatedKey;
		}
		else {
			return null;
		}
		
	}

	@Override
	public KeyDto getKeyById(KeyDto key) {
		
		return keyDao.selectKeyById(key);
	}

}
