package com.mskim.i18l.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mskim.i18l.dto.TranslationDto;

public interface TranslationDao {
	
	int insertTranslation(TranslationDto translation);
	
	List<HashMap<String,Object>> selectTranslationsByKeyId(Map<String, Object> queryParamMap);
	
	TranslationDto selectTranslationByKeyIdAndLocale(TranslationDto translation);
	
	int updateTranslation(TranslationDto translation);
	
}
