package com.baeldung.crawler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.baeldung.config.GlobalConstants;
import com.baeldung.util.Utils;

@Component
public class GitModulesReadmeLinksExtractor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${crawler.refreshReadmeLinks}")
    private boolean refreshReadmeLinks = false;

    @Autowired
    TutorialsRepoCrawlerController tutorialsRepoCrawlerController;

    public void findAndUpdateLinksToReadmeFiles() throws IOException {
        if (!refreshReadmeLinks) {
            return;
        } else {
            tutorialsRepoCrawlerController.startCrawler(CrawlerForIncorrectlyLinkedURLs.class, Runtime.getRuntime().availableProcessors());
            File file = new File(Utils.class.getClassLoader().getResource(GlobalConstants.README_LINKS_FOLDER_PATH + GlobalConstants.README_LINKS_FILE_NAME).getPath());
            Path readmeLiksFilePath = Paths.get(file.getAbsolutePath());
            Files.write(readmeLiksFilePath, "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            for (Object object : tutorialsRepoCrawlerController.getFlaggedURL()) {
                List<String> urlList = (List<String>) object;
                logger.info("List Size:" + urlList.size());
                urlList.forEach(link -> {
                    try {
                        Files.write(readmeLiksFilePath, (link + "\n").getBytes(), StandardOpenOption.APPEND);
                    } catch (IOException e) {                        
                        e.printStackTrace();
                    }
                    logger.info(link);
                });                
            }
            File fileInSrcDirectory;
            if (Files.exists(Paths.get(Utils.getAbsolutePathToFileInSrc(GlobalConstants.README_LINKS_FILE_NAME)), LinkOption.NOFOLLOW_LINKS)) {
                fileInSrcDirectory = new File(Utils.getAbsolutePathToFileInSrc(GlobalConstants.README_LINKS_FILE_NAME));
                Path readmeLiksFilePathInSrcDirectory = Paths.get(fileInSrcDirectory.getAbsolutePath());
                Files.copy(readmeLiksFilePath, readmeLiksFilePathInSrcDirectory, StandardCopyOption.REPLACE_EXISTING);

            }

        }
    }

}
