package com.mskim.i18l.service;

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

}
