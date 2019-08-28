package com.mskim.i18l.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mskim.i18l.dto.KeyDto;
import com.mskim.i18l.dto.TranslationDto;
import com.mskim.i18l.service.I18nService;


@RestController
public class ApiController {

	@Autowired
	private I18nService i18nService;
	
	private final String namePattern = "^[a-z.]*$";
	
	private final List<String> validLocales = Arrays.asList("ko", "en", "ja");

	private Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	@GetMapping("/keys")
	public ResponseEntity<Map<String,Object>> getKey(@RequestParam(value="name", required=false)String name) {
		
		logger.info("GET /keys name=" + name);
		
		if(name != null && !Pattern.matches(namePattern, name)) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
		
		List<HashMap<String,Object>> keys = i18nService.getKeys(name);
		
		if(keys.isEmpty()) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.NOT_FOUND);
		}
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("keys", keys);
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@PostMapping("/keys")
	public ResponseEntity<Map<String,Object>> addKey(@RequestBody @Valid KeyDto key) {

		logger.info("POST /keys	name=" + key.getName());
		
		if(!Pattern.matches(namePattern, key.getName())) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
		
		List<HashMap<String,Object>> keys = i18nService.getKeys(key.getName());
		if(!keys.isEmpty()) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.CONFLICT);
		}
		
		KeyDto insertedKey = i18nService.addKey(key);
		
		if(insertedKey == null) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("key", insertedKey);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@PutMapping(value = "/keys/{keyId}")
	public ResponseEntity<Map<String,Object>> updateCustomer(@PathVariable("keyId") int keyId, @RequestBody @Valid KeyDto key) {
		
		logger.info("PUT /keys	keyId=" + keyId + ", name=" + key.getName());
		
		if(!Pattern.matches(namePattern, key.getName())) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
		
		key.setId(keyId);
		if(i18nService.getKeyById(key) == null) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.NOT_FOUND);
		}
		
		KeyDto updatedKey = i18nService.updateKey(key);

		if(updatedKey == null) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("key", updatedKey);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@PostMapping(value = "/keys/{keyId}/translations/{locale}")
	public ResponseEntity<Map<String,Object>> addTranslation(@PathVariable("keyId") int keyId, @PathVariable("locale") String locale
															, @RequestBody @Valid TranslationDto translation) {
		
		logger.info("POST /keys/{keyId}/translations/{locale}	keyId=" + keyId + ", locale=" + locale + ", value=" + translation.getValue());

		String detectedLocale = i18nService.getLanguageLocale(translation.getValue());
		if(detectedLocale == null || !detectedLocale.equals(locale)) {
			Map<String,Object> messageMap = new HashMap<String,Object>();
			messageMap.put("message", "value and locale are not matched.");
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.BAD_REQUEST);
		}
		
		if(!validLocales.contains(locale)) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
		
		translation.setKeyId(keyId);
		translation.setLocale(locale);
		
		TranslationDto existingTranslation = i18nService.getTranslationByKeyIdAndLocale(translation);
		if(existingTranslation != null) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.CONFLICT);
		}
		
		TranslationDto insertedTranslation = i18nService.addTranslation(translation);
		
		if(insertedTranslation == null) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("translation", insertedTranslation);
		System.out.println("id : " + insertedTranslation.getId());
		System.out.println("key_id : " + insertedTranslation.getKeyId());
		System.out.println("locale : " + insertedTranslation.getLocale());
		System.out.println("value : " + insertedTranslation.getValue());
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@GetMapping("/keys/{keyId}/translations")
	public ResponseEntity<Map<String,Object>> getAllTranslationsByKeyId(@PathVariable("keyId") int keyId) {
		
		logger.info("GET /keys/{keyId}/translations	keyId=" + keyId);
		
		List<HashMap<String,Object>> translations = i18nService.getTranslationsByKeyId(keyId);
		
		if(translations.isEmpty()) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.NOT_FOUND);
		}
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("translations", translations);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@GetMapping("/keys/{keyId}/translations/{locale}")
	public ResponseEntity<Map<String,Object>> getTranslationByKeyIdAndLocale(@PathVariable("keyId") int keyId, 
																			@PathVariable("locale") String locale) {
		
		logger.info("GET /keys/{keyId}/translations{locale}	keyId=" + keyId + ", locale=" + locale);

		if(!validLocales.contains(locale)) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
		
		TranslationDto translation = new TranslationDto();
		translation.setKeyId(keyId);
		translation.setLocale(locale);
		
		TranslationDto selectedTranslation = i18nService.getTranslationByKeyIdAndLocale(translation);
		
		if(selectedTranslation == null) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.NOT_FOUND);
		}
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("translation", selectedTranslation);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@PutMapping(value = "/keys/{keyId}/translations/{locale}")
	public ResponseEntity<Map<String,Object>> updateTranslation(@PathVariable("keyId") int keyId, @PathVariable("locale") String locale, 
																@RequestBody @Valid TranslationDto translation) {
		
		logger.info("PUT /keys/{keyId}/translations/{locale}	keyId=" + keyId + ", locale=" + locale);
		
		String detectedLocale = i18nService.getLanguageLocale(translation.getValue());
		if(detectedLocale == null || !detectedLocale.equals(locale)) {
			Map<String,Object> messageMap = new HashMap<String,Object>();
			messageMap.put("message", "value and locale are not matched.");
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.BAD_REQUEST);
		}
		
		if(!validLocales.contains(locale)) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
		
		translation.setKeyId(keyId);
		translation.setLocale(locale);
		TranslationDto existingTranslation = i18nService.getTranslationByKeyIdAndLocale(translation);
		
		if(existingTranslation == null) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.NOT_FOUND);
		}
		
		TranslationDto updatedTranslation = i18nService.updateTranslation(translation);
		
		if(updatedTranslation == null) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("translation", updatedTranslation);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@GetMapping("/language_detect")
	public ResponseEntity<Map<String,Object>> getLanguageLocale(@RequestParam(value="message", required=true)String message) {
		
		logger.info("GET /language_detect	message=" + message);
		
		String locale = i18nService.getLanguageLocale(message);
		if(locale == null) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.NOT_FOUND);
		}
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("locale", locale);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
}
