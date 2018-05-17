package com.nocilliswine.serviceimpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nocilliswine.service.WineBuyingService;

@Service
public class WineBuyingServiceImpl implements WineBuyingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WineBuyingServiceImpl.class);

	@Value("${tmp.file.location}")
	String tempFileLocation;

	@Value("${thread.pool.size}")
	int threadPoolSize;

	final static String FILE_NAME_POSTSTRING = ".txt";

	final static String OUTPUT_FILE_NAME_POSTSTRING = "_OUTPUT.txt";
	
	volatile Map<String, String> personWineMap = new HashMap<>();
	
	volatile Set<String> wineSet= Collections.synchronizedSet(new HashSet<String>()); 
	
	private ExecutorService getExecutor() {
		return Executors.newFixedThreadPool(threadPoolSize, r -> {
			return new Thread(r);
		});
	}

	// Following method will read each record from file store it in db using multi theading
	@Override
	public File readAndGetBuyingList(MultipartFile file) throws Exception {
		ExecutorService executorService=getExecutor();
		File tempFile = new File(tempFileLocation + file.getOriginalFilename());
		try {
			file.transferTo(tempFile);
			BufferedReader br = new BufferedReader(new FileReader(tempFile));
			String line;
			while ((line = br.readLine()) != null) {// reading file line by line
				String tmpLine = line;
				executorService.execute(() -> {
					try {
						StringTokenizer token = new StringTokenizer(tmpLine);
						String personId = token.nextToken();
						String wineId = token.nextToken();
						synchronized (personWineMap) {
							if (wineSet.add(wineId)) {
								LOGGER.info("Storing person id {} and wine id {} into map", personId, wineId);
								if (personWineMap.containsKey(personId)) {
									String wines[] = personWineMap.get(personId).split(",");
									if (wines == null || wines.length < 3) {// checking person has already taken the 3 bottles or not
											LOGGER.info("Adding person id :{} with value : {}", personId, wineId);
											personWineMap.put(personId, personWineMap.get(personId) + "," + wineId);
										}
									} else {
										personWineMap.put(personId, wineId);
									}
								}
							}
					} catch (Exception e) {
						LOGGER.error("Exception while processing file line : {}", tmpLine, e);
					}
				});

			}
			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			return generateOutputFile(file.getOriginalFilename());// generating output file
		} catch (Exception e) {
			LOGGER.error("Exception while processing file", e);
			throw e;
		}
	}

	// Following method will generate the output file
	@Override
	public File generateOutputFile(String fileName) throws Exception {
		String tmpFilePath = tempFileLocation + fileName.replace(FILE_NAME_POSTSTRING, OUTPUT_FILE_NAME_POSTSTRING);
		try (Writer writer = new BufferedWriter(new FileWriter(new File(tmpFilePath)));) {
			long totalRecords = personWineMap.size();
			LOGGER.info("Total count of final wine buying list is : {}", totalRecords);
			writer.write("Number of Wine bottles sold : " + totalRecords + "\n");
			for (Map.Entry<String, String> entry : personWineMap.entrySet()) {
				String wines[] = entry.getValue().split(",");
				if(wines==null){
				writer.write(entry.getKey() + "\t" + entry.getValue() + "\n");// writing to file
				}
				else{
					for(String wineId:wines){
						writer.write(entry.getKey() + "\t" + wineId + "\n");// writing to file
					}
				}
			}
			writer.flush();
			return new File(tmpFilePath);
		} catch (Exception e) {
			LOGGER.error("exception while generating output file", e);
			throw e;
		}
	}
	
	@Override
	public File readAndGetBuyingListWithoutThreads(MultipartFile file) throws Exception {
		File tempFile = new File(tempFileLocation + file.getOriginalFilename());
		try {
			file.transferTo(tempFile);
			BufferedReader br = new BufferedReader(new FileReader(tempFile));
			String line;
			while ((line = br.readLine()) != null) {// reading file line by line
					try {
						StringTokenizer token = new StringTokenizer(line);
						String personId = token.nextToken();
						String wineId = token.nextToken();
							if (wineSet.add(wineId)) {
								LOGGER.info("Storing person id {} and wine id {} into map", personId, wineId);
									if (personWineMap.containsKey(personId)) {
										String wines[] = personWineMap.get(personId).split(",");
										if (wines == null || wines.length < 3) {// checking person has already taken the 3 bottles or not
											LOGGER.info("Adding person id :{} with value : {}", personId, wineId);
											personWineMap.put(personId, personWineMap.get(personId) + "," + wineId);
										}
									} else {
										personWineMap.put(personId, wineId);
									}
							}
					} catch (Exception e) {
						LOGGER.error("Exception while processing file line : {}", line, e);
					}
			}
			return generateOutputFile(file.getOriginalFilename());// generating output file
		} catch (Exception e) {
			LOGGER.error("Exception while processing file", e);
			throw e;
		}
	}

}
