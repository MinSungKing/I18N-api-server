package com.mskim.i18l.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import com.mskim.i18l.service.I18nService;


@RestController
public class ApiController {

	@Autowired
	private I18nService i18nService;
	
	private String namePattern = "^[a-z.]*$";

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
	public ResponseEntity<Map<String,Object>> addKey(@RequestBody KeyDto key) {

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
	public ResponseEntity<Map<String,Object>> updateCustomer(@PathVariable("keyId") int keyId, @RequestBody KeyDto key) {
		
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
}
