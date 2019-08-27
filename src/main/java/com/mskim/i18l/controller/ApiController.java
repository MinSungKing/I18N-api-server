package com.mskim.i18l.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mskim.i18l.dto.KeyDto;
import com.mskim.i18l.service.I18nService;

@RestController
public class ApiController {

	@Autowired
	private I18nService i18nService;

	private Logger logger = LoggerFactory.getLogger(ApiController.class);

	@GetMapping("/keys")
	public Map<String,Object> addKey(@RequestParam(value="name")String name) {
		
		logger.info("GET /keys name=" + name);

		List<HashMap<String,Object>> keys = i18nService.getKeys(name);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("keys", keys);
		
		return resultMap;
	}

}
