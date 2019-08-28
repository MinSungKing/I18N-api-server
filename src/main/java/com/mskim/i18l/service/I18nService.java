package com.mskim.i18l.service;

import java.util.HashMap;
import java.util.List;

import com.mskim.i18l.dto.KeyDto;
import com.mskim.i18l.dto.TranslationDto;

public interface I18nService {
	
	KeyDto addKey(KeyDto key);
	List<HashMap<String,Object>> getKeys(String name);
	KeyDto getKeyById(KeyDto key);
	KeyDto updateKey(KeyDto key);
	
	
	TranslationDto addTranslation(TranslationDto translation);
	List<HashMap<String,Object>> getTranslationsByKeyId(int keyId);
	TranslationDto getTranslationByKeyIdAndLocale(TranslationDto translation);
	TranslationDto updateTranslation(TranslationDto translation);
	String getLanguageLocale(String message);

}
