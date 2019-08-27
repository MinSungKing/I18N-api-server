package com.mskim.i18l.service;

import java.util.HashMap;
import java.util.List;

import com.mskim.i18l.dto.KeyDto;

public interface I18nService {
	
	KeyDto addKey(KeyDto key);
	List<HashMap<String,Object>> getKeys(String name);
	KeyDto getKeyById(KeyDto key);
	KeyDto updateKey(KeyDto key);

}
