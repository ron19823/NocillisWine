package com.nocilliswine.serviceimpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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

	final static String FILE_NAME_POSTSTRING = ".txt";

	final static String OUTPUT_FILE_NAME_POSTSTRING = "_OUTPUT.txt";

	// Following method will read each record from file store it in db
	@Override
	public File readAndGetBuyingList(MultipartFile file) throws Exception {
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
					if (wineBuyingListDao.countByPersonId(personId) < 3) {// checking person has already taken the 3 bottles or not
						LOGGER.info("Storing person id {} and wine id {} into db", personId, wineId);
						wineBuyingListDao.save(new WineBuyingList(wineId, personId));
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

	// Following method will generate the output file
	@Override
	public File generateOutputFile(String fileName) throws Exception {
		try (Writer writer = new BufferedWriter(
				new FileWriter(new File(tempFileLocation + fileName.replace(".txt", "_OUTPUT.txt"))));) {
			long totalRecords = wineBuyingListDao.count();
			LOGGER.info("Total count of final wine buying list is : {}", totalRecords);
			writer.write("Number of Wine bottles sold : " + totalRecords + "\n");
			long loopCount = totalRecords / pageSize;
			if (totalRecords % pageSize != 0) {// checking if the total recods count gives a remainder 
				loopCount++;//if devided by page size if yes then remaining pages need to be considered
			}
			LOGGER.info("Total loop count for writing page is : {}", loopCount);
			for (int i = 0; i < loopCount; i++) {
				Page<WineBuyingList> wineBuyingLists = wineBuyingListDao.findAll(new PageRequest(i, pageSize));// getting paginated records
				LOGGER.info("values for page number :{} are : {}", wineBuyingLists);
				for (WineBuyingList content : wineBuyingLists) {
					writer.write(content.getPersonId() + "\t" + content.getWineId() + "\n");//writing to file
				}
			}
			writer.flush();
			return new File(tempFileLocation + fileName.replace(FILE_NAME_POSTSTRING, OUTPUT_FILE_NAME_POSTSTRING));
		} catch (Exception e) {
			LOGGER.error("exception while generating output file", e);
			throw e;
		}

	}

}
