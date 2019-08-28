package com.mskim.i18l.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mskim.i18l.dao.KeyDao;
import com.mskim.i18l.dao.TranslationDao;
import com.mskim.i18l.dto.KeyDto;
import com.mskim.i18l.dto.TranslationDto;

@Service
public class I18nServiceImpl implements I18nService {

	@Autowired
	private KeyDao keyDao;
	
	@Autowired
	private TranslationDao translationDao;
	
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

	@Override
	public TranslationDto addTranslation(TranslationDto translation) {
		
		if(translationDao.insertTranslation(translation) > 0) {
			TranslationDto insertedTranslation = translationDao.selectTranslationByKeyIdAndLocale(translation);
			return insertedTranslation;
		}
		else {
			return null;
		}
	}

	@Override
	public List<HashMap<String, Object>> getTranslationsByKeyId(int keyId) {
		
		Map<String,Object> queryParamMap = new HashMap<>();
		queryParamMap.put("keyId", keyId);
		
		return translationDao.selectTranslationsByKeyId(queryParamMap);
	}

	@Override
	public TranslationDto getTranslationByKeyIdAndLocale(TranslationDto translation) {
		
		return translationDao.selectTranslationByKeyIdAndLocale(translation);
	}

	@Override
	public TranslationDto updateTranslation(TranslationDto translation) {
		
		if(translationDao.updateTranslation(translation) > 0) {
			TranslationDto updatedTranslation = translationDao.selectTranslationByKeyIdAndLocale(translation);
			return updatedTranslation;
		}
		else {
			return null;
		}
	}

}
