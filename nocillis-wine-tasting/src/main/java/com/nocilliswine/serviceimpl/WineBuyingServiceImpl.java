package com.nocilliswine.serviceimpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.nocilliswine.dao.WineBuyingListDao;
import com.nocilliswine.model.WineBuyingList;
import com.nocilliswine.service.WineBuyingService;

@Service
public class WineBuyingServiceImpl implements WineBuyingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WineBuyingServiceImpl.class);

	@Value("${tmp.file.location}")
	String tempFileLocation;

	@Value("${page.size}")
	int pageSize;

	@Autowired
	WineBuyingListDao wineBuyingListDao;
	
	volatile Map<String, String> personWineMap = new HashMap<>();
	
	volatile Set<String> wineSet= new HashSet<>(); 

	final static String FILE_NAME_POSTSTRING = ".txt";

	final static String OUTPUT_FILE_NAME_POSTSTRING = "_OUTPUT.txt";
	
	private ExecutorService getExecutor() {
		return Executors.newFixedThreadPool(1, (r) -> {
			return new Thread(r);
		});
	}

	// Following method will read each record from file store it in db
	@Override
	public File readAndGetBuyingList(MultipartFile file) throws Exception {
		File tempFile = new File(tempFileLocation + file.getOriginalFilename());
		try {
			file.transferTo(tempFile);
			BufferedReader br = new BufferedReader(new FileReader(tempFile));
			String line;
			while ((line = br.readLine()) != null) {// reading file line by line
				String tmpLine = line;
				getExecutor().execute(() -> {
					try {
						StringTokenizer token = new StringTokenizer(tmpLine);
						String personId = token.nextToken();
						String wineId = token.nextToken();
						if (wineSet.contains(wineId)) {
							return;
						}
						wineSet.add(wineId);
						LOGGER.info("Storing person id {} and wine id {} into map", personId, wineId);
						if (personWineMap.containsKey(personId)) {
							if(StringUtils.split(personWineMap.get(personId),",").length<3){// checking person has already taken the 3 bottles or not
								LOGGER.info("Adding person id :{} with value : {}", personId, wineId);
								personWineMap.put(personId, personWineMap.get(personId) + "," + wineId);
							}
						} else {
							personWineMap.put(personId, wineId);
						}

					} catch (Exception e) {
						LOGGER.error("Exception while processing file line : {}", tmpLine, e);
					}
				});

			}
			return generateOutputFile(file.getOriginalFilename());// generating
																	// output
																	// file
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
				writer.write(entry.getKey() + "\t" + entry.getValue() + "\n");// writing to file
			}
			writer.flush();
			return new File(tmpFilePath);
		} catch (Exception e) {
			LOGGER.error("exception while generating output file", e);
			throw e;
		}

	}
	
}
