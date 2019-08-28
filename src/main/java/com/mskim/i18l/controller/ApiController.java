package com.mskim.i18l.controller;

import java.util.Map;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	private Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	@GetMapping("/keys")
	public ResponseEntity<Map<String,Object>> getKeys(@RequestParam(value="name", required=false)String name) {
		
		logger.info("GET /keys	name=" + name);
		
		return i18nService.getKeys(name);
	}
	
	@PostMapping("/keys")
	public ResponseEntity<Map<String,Object>> addKey(@RequestBody @Valid KeyDto key) {

		logger.info("POST /keys	name=" + key.getName());
		
		return i18nService.addKey(key);
	}
	
	@PutMapping(value = "/keys/{keyId}")
	public ResponseEntity<Map<String,Object>> updateKey(@PathVariable("keyId") int keyId, @RequestBody @Valid KeyDto key) {
		
		logger.info("PUT /keys	keyId=" + keyId + ", name=" + key.getName());
		
		return i18nService.updateKey(keyId, key);
	}
	
	@PostMapping(value = "/keys/{keyId}/translations/{locale}")
	public ResponseEntity<Map<String,Object>> addTranslation(@PathVariable("keyId") int keyId, @PathVariable("locale") String locale
															, @RequestBody @Valid TranslationDto translation) {
		
		logger.info("POST /keys/{keyId}/translations/{locale}	keyId=" + keyId + ", locale=" + locale + ", value=" + translation.getValue());
		
		return i18nService.addTranslation(keyId, locale, translation);
	}
	
	@GetMapping("/keys/{keyId}/translations")
	public ResponseEntity<Map<String,Object>> getAllTranslationsByKeyId(@PathVariable("keyId") int keyId) {
		
		logger.info("GET /keys/{keyId}/translations	keyId=" + keyId);
		
		return i18nService.getTranslationsByKeyId(keyId);
	}
	
	@GetMapping("/keys/{keyId}/translations/{locale}")
	public ResponseEntity<Map<String,Object>> getTranslationByKeyIdAndLocale(@PathVariable("keyId") int keyId, 
																			@PathVariable("locale") String locale) {
		
		logger.info("GET /keys/{keyId}/translations{locale}	keyId=" + keyId + ", locale=" + locale);
		
		return i18nService.getTranslationByKeyIdAndLocale(keyId, locale);
	}
	
	@PutMapping(value = "/keys/{keyId}/translations/{locale}")
	public ResponseEntity<Map<String,Object>> updateTranslation(@PathVariable("keyId") int keyId, @PathVariable("locale") String locale, 
																@RequestBody @Valid TranslationDto translation) {
		
		logger.info("PUT /keys/{keyId}/translations/{locale}	keyId=" + keyId + ", locale=" + locale);
		
		return i18nService.updateTranslation(keyId, locale, translation);
	}
	
	@GetMapping("/language_detect")
	public ResponseEntity<Map<String,Object>> getLanguageLocale(@RequestParam(value="message", required=true)String message) {
		
		logger.info("GET /language_detect	message=" + message);
		
		return i18nService.getLanguageLocale(message);
	}
}
