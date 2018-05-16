package com.nocilliswine.service;

import java.io.File;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Rohan Sharma
 *
 */
public interface WineBuyingService {

	File readAndGetBuyingList(MultipartFile file) throws Exception;

	File generateOutputFile(String fileName) throws Exception;
}
