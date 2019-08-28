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
		
		logger.info("GET /keys	name=" + name);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> messageMap = new HashMap<String,Object>();
		String message = "";
		
		if(name != null && !Pattern.matches(namePattern, name)) {
			message = "Key with name [" + name + "] is not valid. Only small letters and dot(.) are allowed.";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.BAD_REQUEST);
		}
		
		List<HashMap<String,Object>> keys = i18nService.getKeys(name);
		
		if(keys.isEmpty()) {
			message = "Can not find any key with name [" + name + "].";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.NOT_FOUND);
		}
		
		resultMap.put("keys", keys);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@PostMapping("/keys")
	public ResponseEntity<Map<String,Object>> addKey(@RequestBody @Valid KeyDto key) {

		logger.info("POST /keys	name=" + key.getName());
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> messageMap = new HashMap<String,Object>();
		String message = "";
		
		if(!Pattern.matches(namePattern, key.getName())) {
			message = "name [" + key.getName() + "] is not valid. Only small letters and dot(.) are allowed.";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.BAD_REQUEST);
		}
		
		List<HashMap<String,Object>> keys = i18nService.getKeys(key.getName());
		if(!keys.isEmpty()) {
			message = "name [" + key.getName() + "] already exists.";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.CONFLICT);
		}
		
		KeyDto insertedKey = i18nService.addKey(key);
		
		if(insertedKey == null) {
			message = "Failed to insert Key with name [" + key.getName() + "].";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		resultMap.put("key", insertedKey);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@PutMapping(value = "/keys/{keyId}")
	public ResponseEntity<Map<String,Object>> updateKey(@PathVariable("keyId") int keyId, @RequestBody @Valid KeyDto key) {
		
		logger.info("PUT /keys	keyId=" + keyId + ", name=" + key.getName());
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> messageMap = new HashMap<String,Object>();
		String message = "";

		if(!Pattern.matches(namePattern, key.getName())) {
			message = "name [" + key.getName() + "] is not valid. Only small letters and dot(.) are allowed.";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.BAD_REQUEST);
		}
		
		key.setId(keyId);
		KeyDto existingKey = i18nService.getKeyById(key);
		if(existingKey == null) {
			message = "Can not find any key with keyId [" + keyId + "].";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.NOT_FOUND);
		}
		
		KeyDto updatedKey = i18nService.updateKey(key);

		if(updatedKey == null) {
			message = "Failed to update key with keyId [" + keyId + "].";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		resultMap.put("key", updatedKey);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@PostMapping(value = "/keys/{keyId}/translations/{locale}")
	public ResponseEntity<Map<String,Object>> addTranslation(@PathVariable("keyId") int keyId, @PathVariable("locale") String locale
															, @RequestBody @Valid TranslationDto translation) {
		
		logger.info("POST /keys/{keyId}/translations/{locale}	keyId=" + keyId + ", locale=" + locale + ", value=" + translation.getValue());

		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> messageMap = new HashMap<String,Object>();
		String message = "";
		
		String detectedLocale = i18nService.getLanguageLocale(translation.getValue());
		if(detectedLocale == null || !detectedLocale.equals(locale)) {
			message = "value [" + translation.getValue() + "] and locale [" + locale + "] are not matched.";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.BAD_REQUEST);
		}
		
		if(!validLocales.contains(locale)) {
			message = "locale [" + locale + "] is not allowed. ['ko', 'en', 'ja'] are valid.";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
		
		translation.setKeyId(keyId);
		translation.setLocale(locale);
		
		TranslationDto existingTranslation = i18nService.getTranslationByKeyIdAndLocale(translation);
		if(existingTranslation != null) {
			message = "Translation with keyId [" + keyId + "] and locale [" + locale + "] already exists.";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.CONFLICT);
		}
		
		TranslationDto insertedTranslation = i18nService.addTranslation(translation);
		
		if(insertedTranslation == null) {
			message = "Failed to insert Translation with keyId [" + keyId + "] and locale [" + locale + "].";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		resultMap.put("translation", insertedTranslation);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@GetMapping("/keys/{keyId}/translations")
	public ResponseEntity<Map<String,Object>> getAllTranslationsByKeyId(@PathVariable("keyId") int keyId) {
		
		logger.info("GET /keys/{keyId}/translations	keyId=" + keyId);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> messageMap = new HashMap<String,Object>();
		String message = "";
		
		List<HashMap<String,Object>> translations = i18nService.getTranslationsByKeyId(keyId);
		
		if(translations.isEmpty()) {
			message = "Can not find any translation with keyId [" + keyId + "].";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.NOT_FOUND);
		}
		
		resultMap.put("translations", translations);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@GetMapping("/keys/{keyId}/translations/{locale}")
	public ResponseEntity<Map<String,Object>> getTranslationByKeyIdAndLocale(@PathVariable("keyId") int keyId, 
																			@PathVariable("locale") String locale) {
		
		logger.info("GET /keys/{keyId}/translations{locale}	keyId=" + keyId + ", locale=" + locale);

		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> messageMap = new HashMap<String,Object>();
		String message = "";
		
		if(!validLocales.contains(locale)) {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
		
		TranslationDto translation = new TranslationDto();
		translation.setKeyId(keyId);
		translation.setLocale(locale);
		
		TranslationDto selectedTranslation = i18nService.getTranslationByKeyIdAndLocale(translation);
		
		if(selectedTranslation == null) {
			message = "Can not find any translation with keyId [" + keyId + "] and locale [" + locale + "].";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.NOT_FOUND);
		}
		
		resultMap.put("translation", selectedTranslation);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@PutMapping(value = "/keys/{keyId}/translations/{locale}")
	public ResponseEntity<Map<String,Object>> updateTranslation(@PathVariable("keyId") int keyId, @PathVariable("locale") String locale, 
																@RequestBody @Valid TranslationDto translation) {
		
		logger.info("PUT /keys/{keyId}/translations/{locale}	keyId=" + keyId + ", locale=" + locale);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> messageMap = new HashMap<String,Object>();
		String message = "";
		
		String detectedLocale = i18nService.getLanguageLocale(translation.getValue());
		if(detectedLocale == null || !detectedLocale.equals(locale)) {
			message = "value [" + translation.getValue() + "] and locale [" + locale + "] are not matched.";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.BAD_REQUEST);
		}
		
		if(!validLocales.contains(locale)) {
			message = "locale [" + locale + "] is not allowed. ['ko', 'en', 'ja'] are valid.";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
		
		translation.setKeyId(keyId);
		translation.setLocale(locale);
		TranslationDto existingTranslation = i18nService.getTranslationByKeyIdAndLocale(translation);
		
		if(existingTranslation == null) {
			message = "Can not find any translation with keyId [" + keyId + "] and locale [" + locale + "].";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.NOT_FOUND);
		}
		
		TranslationDto updatedTranslation = i18nService.updateTranslation(translation);
		
		if(updatedTranslation == null) {
			message = "Failed to update Translation with keyId [" + keyId + "] and locale [" + locale + "].";
			logger.info(message);
			messageMap.put("message", message);
			return new ResponseEntity<Map<String,Object>>(messageMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		resultMap.put("translation", updatedTranslation);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	@GetMapping("/language_detect")
	public ResponseEntity<Map<String,Object>> getLanguageLocale(@RequestParam(value="message", required=true)String message) {
		
		logger.info("GET /language_detect	message=" + message);

		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> messageMap = new HashMap<String,Object>();
		String responseMessage = "";

		String locale = i18nService.getLanguageLocale(message);
		if(locale == null) {
			responseMessage = "Can not detect locale of message [" + message + "]";
			logger.info(responseMessage);
			messageMap.put("message", responseMessage);
			return new ResponseEntity<Map<String,Object>>(HttpStatus.NOT_FOUND);
		}
		
		resultMap.put("locale", locale);
		
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
}
