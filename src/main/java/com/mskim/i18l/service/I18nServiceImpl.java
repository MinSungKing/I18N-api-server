package com.mskim.i18l.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mskim.i18l.dao.KeyDao;
import com.mskim.i18l.dao.TranslationDao;
import com.mskim.i18l.dto.KeyDto;
import com.mskim.i18l.dto.TranslationDto;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.errors.APIError;

@Service
public class I18nServiceImpl implements I18nService {
	
	private Logger logger = LoggerFactory.getLogger(I18nServiceImpl.class);

	@Autowired
	private KeyDao keyDao;
	
	@Autowired
	private TranslationDao translationDao;
	
	@Value("${detectlanguage.apikey}")
	private String detectLanguageApiKey;
	
	private final String namePattern = "^[a-z.]*$";
	
	private final List<String> validLocales = Arrays.asList("ko", "en", "ja");

	@Override
	public ResponseEntity<Map<String,Object>> getKeys(String name) {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String message = "";
		
		if(name != null && !Pattern.matches(namePattern, name)) {
			message = "Key with name [" + name + "] is not valid. Only small letters and dot(.) are allowed.";
			return getResponseForExceptionCase(message, HttpStatus.BAD_REQUEST);
		}
		
		Map<String,String> queryParamMap = new HashMap<>();
		queryParamMap.put("name", name);
		List<HashMap<String,Object>> keys = keyDao.selectKeys(queryParamMap);
		
		if(keys.isEmpty()) {
			message = "Can not find any key with name [" + name + "].";
			return getResponseForExceptionCase(message, HttpStatus.NOT_FOUND);
		}
		
		resultMap.put("keys", keys);
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<Map<String,Object>> addKey(KeyDto key) {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String message = "";
		
		if(!Pattern.matches(namePattern, key.getName())) {
			message = "name [" + key.getName() + "] is not valid. Only small letters and dot(.) are allowed.";
			return getResponseForExceptionCase(message, HttpStatus.BAD_REQUEST);
		}
		
		KeyDto selectedKey = keyDao.selectKeyByName(key);
		if(selectedKey != null) {
			message = "name [" + key.getName() + "] already exists.";
			return getResponseForExceptionCase(message, HttpStatus.CONFLICT);
		}
		
		if(keyDao.insertKey(key) > 0) {	//insert success
			KeyDto insertedKey = keyDao.selectKeyByName(key);
			resultMap.put("key", insertedKey);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.CREATED);
		}
		else {
			message = "Failed to insert Key with name [" + key.getName() + "].";
			return getResponseForExceptionCase(message, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}

	@Override
	public ResponseEntity<Map<String,Object>> updateKey(int keyId, KeyDto key) {

		Map<String,Object> resultMap = new HashMap<String,Object>();
		String message = "";

		if(!Pattern.matches(namePattern, key.getName())) {
			message = "name [" + key.getName() + "] is not valid. Only small letters and dot(.) are allowed.";
			return getResponseForExceptionCase(message, HttpStatus.BAD_REQUEST);
		}
		
		key.setId(keyId);
		KeyDto targetKey = keyDao.selectKeyById(key);
		
		if(targetKey == null) {
			message = "Can not find any key with keyId [" + keyId + "].";
			return getResponseForExceptionCase(message, HttpStatus.NOT_FOUND);
		}
		
		if(keyDao.updateKey(key) > 0) {	//update success
			KeyDto updatedKey = keyDao.selectKeyById(key);
			resultMap.put("key", updatedKey);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}
		else {
			message = "Failed to update key with keyId [" + keyId + "].";
			return getResponseForExceptionCase(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@Override
	public ResponseEntity<Map<String,Object>> addTranslation(int keyId, String locale, TranslationDto translation) {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String message = "";
		
		if(!validLocales.contains(locale)) {
			message = "locale [" + locale + "] is not allowed. [ko, en, ja] are valid.";
			return getResponseForExceptionCase(message, HttpStatus.BAD_REQUEST);
		}
		
		String detectedLocale = getLanguageLocaleWithApi(translation.getValue());
		if(detectedLocale == null || !detectedLocale.equals(locale)) {
			message = "value [" + translation.getValue() + "] and locale [" + locale + "] are not matched.";
			return getResponseForExceptionCase(message, HttpStatus.BAD_REQUEST);
		}
		
		KeyDto key = new KeyDto();
		key.setId(keyId);
		KeyDto existingKey = keyDao.selectKeyById(key);
		
		if(existingKey == null) {
			message = "Can not find any key with keyId [" + keyId + "].";
			return getResponseForExceptionCase(message, HttpStatus.NOT_FOUND);
		}
		
		translation.setKeyId(keyId);
		translation.setLocale(locale);
		TranslationDto targetTranslation = translationDao.selectTranslationByKeyIdAndLocale(translation);
		
		if(targetTranslation != null) {
			message = "Translation with keyId [" + keyId + "] and locale [" + locale + "] already exists.";
			return getResponseForExceptionCase(message, HttpStatus.CONFLICT);
		}
		
		if(translationDao.insertTranslation(translation) > 0) {	//insert success
			TranslationDto insertedTranslation = translationDao.selectTranslationByKeyIdAndLocale(translation);
			
			resultMap.put("translation", insertedTranslation);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.CREATED);
		}
		else {
			message = "Failed to insert Translation with keyId [" + keyId + "] and locale [" + locale + "].";
			return getResponseForExceptionCase(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<Map<String,Object>> getTranslationsByKeyId(int keyId) {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String message = "";
		
		Map<String,Object> queryParamMap = new HashMap<>();
		queryParamMap.put("keyId", keyId);
		List<HashMap<String,Object>> translations = translationDao.selectTranslationsByKeyId(queryParamMap);
		
		if(translations.isEmpty()) {
			message = "Can not find any translation with keyId [" + keyId + "].";
			return getResponseForExceptionCase(message, HttpStatus.NOT_FOUND);
		}
		
		resultMap.put("translations", translations);
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Map<String,Object>> getTranslationByKeyIdAndLocale(int keyId, String locale) {

		Map<String,Object> resultMap = new HashMap<String,Object>();
		String message = "";
		
		if(!validLocales.contains(locale)) {
			message = "locale [" + locale + "] is not allowed. [ko, en, ja] are valid.";
			return getResponseForExceptionCase(message, HttpStatus.BAD_REQUEST);
		}
		
		TranslationDto translation = new TranslationDto();
		translation.setKeyId(keyId);
		translation.setLocale(locale);
		
		TranslationDto selectedTranslation = translationDao.selectTranslationByKeyIdAndLocale(translation);
		
		if(selectedTranslation == null) {
			message = "Can not find any translation with keyId [" + keyId + "] and locale [" + locale + "].";
			return getResponseForExceptionCase(message, HttpStatus.NOT_FOUND);
		}
		
		resultMap.put("translation", selectedTranslation);
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Map<String,Object>> updateTranslation(int keyId, String locale, TranslationDto translation) {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String message = "";
		
		if(!validLocales.contains(locale)) {
			message = "locale [" + locale + "] is not allowed. [ko, en, ja] are valid.";
			return getResponseForExceptionCase(message, HttpStatus.BAD_REQUEST);
		}
		
		String detectedLocale = getLanguageLocaleWithApi(translation.getValue());
		if(detectedLocale == null || !detectedLocale.equals(locale)) {
			message = "value [" + translation.getValue() + "] and locale [" + locale + "] are not matched.";
			return getResponseForExceptionCase(message, HttpStatus.BAD_REQUEST);
		}
		
		translation.setKeyId(keyId);
		translation.setLocale(locale);
		TranslationDto targetTranslation = translationDao.selectTranslationByKeyIdAndLocale(translation);
		
		if(targetTranslation == null) {
			message = "Can not find any translation with keyId [" + keyId + "] and locale [" + locale + "].";
			return getResponseForExceptionCase(message, HttpStatus.NOT_FOUND);
		}
		
		if(translationDao.updateTranslation(translation) > 0) {	//update success
			TranslationDto updatedTranslation = translationDao.selectTranslationByKeyIdAndLocale(translation);
			resultMap.put("translation", updatedTranslation);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}
		else {
			message = "Failed to update Translation with keyId [" + keyId + "] and locale [" + locale + "].";
			return getResponseForExceptionCase(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Override
	public ResponseEntity<Map<String,Object>> getLanguageLocale(String message){
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String responseMessage = "";
		String locale = getLanguageLocaleWithApi(message);
		
		if(locale == null) {
			responseMessage = "Can not detect locale of message [" + message + "]";
			return getResponseForExceptionCase(responseMessage, HttpStatus.NOT_FOUND);
		}
		
		resultMap.put("locale", locale);
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	private String getLanguageLocaleWithApi(String message) {
		
		DetectLanguage.apiKey = detectLanguageApiKey;
		
		String locale = null;
		try {
			locale = DetectLanguage.simpleDetect(message);
			logger.info("Language locale of " + message + " : " + locale);
		} catch (APIError e) {
			logger.error(e.toString());
		}
		
		return locale;
	}

	private ResponseEntity<Map<String,Object>> getResponseForExceptionCase(String message, HttpStatus httpStatus){
		
		Map<String,Object> messageMap = new HashMap<String,Object>();
		logger.info(message);
		messageMap.put("message", message);
		return new ResponseEntity<Map<String,Object>>(messageMap, httpStatus);
	}

}
