package com.nocilliswine.controller;

import java.io.File;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nocilliswine.exception.BadRequestException;
import com.nocilliswine.service.WineBuyingService;

/**
 * @author Rohan Sharma
 *
 */
@RestController
@RequestMapping(value = "/wineTasting")
public class Controller {
	private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

	@Autowired
	WineBuyingService wineBuyingService;

	@RequestMapping(value = "/generateBuyingList", method = RequestMethod.POST)
	public ResponseEntity<InputStreamResource> generateBuyingList(@RequestBody MultipartFile file, @RequestParam(name="useMultiThreading", required=false, defaultValue="0")int multiThreading) throws Exception {
		if (file == null) {
			LOGGER.info("received null file for processing");
			throw new BadRequestException("no file sent for processing");
		}
		LOGGER.info("received {} file for processing", file.getOriginalFilename());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		File outputFile = null;
		outputFile=multiThreading>0?wineBuyingService.readAndGetBuyingList(file): wineBuyingService.readAndGetBuyingListWithoutThreads(file);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(outputFile));
		return ResponseEntity.ok().headers(headers).contentLength(outputFile.length())
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}
}
