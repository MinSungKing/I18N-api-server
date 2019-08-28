package com.mskim.i18l.service;

import java.util.Map;
import org.springframework.http.ResponseEntity;

import com.mskim.i18l.dto.KeyDto;
import com.mskim.i18l.dto.TranslationDto;

public interface I18nService {
	
	ResponseEntity<Map<String, Object>> addKey(KeyDto key);
	
	ResponseEntity<Map<String, Object>> getKeys(String name);
		
	ResponseEntity<Map<String, Object>> updateKey(int keyId, KeyDto key);
	
	ResponseEntity<Map<String,Object>> addTranslation(int keyId, String locale, TranslationDto translation);
	
	ResponseEntity<Map<String,Object>> getTranslationsByKeyId(int keyId);
	
	ResponseEntity<Map<String,Object>> getTranslationByKeyIdAndLocale(int keyId, String locale);
	
	ResponseEntity<Map<String,Object>> updateTranslation(int keyId, String locale, TranslationDto translation);
	
	ResponseEntity<Map<String, Object>> getLanguageLocale(String message);


}
