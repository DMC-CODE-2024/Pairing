package com.eirs.pairs.service;

import com.eirs.pairs.config.FileConfig;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FilesServiceImpl implements FilesService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FileConfig fileConfig;

    @Override
    public File[] getFiles(String filePattern) {
        File dir = new File(fileConfig.getEdrFilesFolder());
        FileFilter fileFilter = new WildcardFileFilter("*" + filePattern + "*.csv");
        File[] files = dir.listFiles(fileFilter);
        return files;
    }


    public void moveFile(File file) {
        try {
            log.info("Moving File:{} to {}", file.toURI(), fileConfig.getEdrMoveFolder());
            Files.move(Paths.get(file.toURI()), Paths.get(fileConfig.getEdrMoveFolder() + "/" + file.getName()));
            log.info("Moved File:{} to {}", file.toURI(), fileConfig.getEdrMoveFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveFileToCompleted(File file) {
        try {
            log.info("Moving File:{} to {}", file.toURI(), fileConfig.getEdrCompletedFolder());
            Files.move(Paths.get(file.toURI()), Paths.get(fileConfig.getEdrCompletedFolder() + "/" + file.getName()));
            log.info("Moved File:{} to {}", file.toURI(), fileConfig.getEdrCompletedFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
