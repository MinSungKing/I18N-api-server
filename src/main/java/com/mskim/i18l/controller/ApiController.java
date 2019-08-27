package com.mskim.i18l.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mskim.i18l.dto.KeyDto;
import com.mskim.i18l.service.I18nService;

@RestController
public class ApiController {

	@Autowired
	private I18nService i18nService;

	private Logger logger = LoggerFactory.getLogger(ApiController.class);

	@PostMapping("/keys")
	public String addKey(@RequestBody KeyDto key) {

		logger.info("#######################################");
		logger.info("Id" + String.valueOf(key.getId()));
		logger.info(key.getName());
		logger.info("#######################################");

		i18nService.addKey(key);
		
		return "Good";
	}

}
