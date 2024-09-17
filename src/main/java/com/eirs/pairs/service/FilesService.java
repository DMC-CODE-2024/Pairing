package com.eirs.pairs.service;

import java.io.File;

public interface FilesService {

    File[] getFiles(String filePattern);

    void moveFile(File file);

    void moveFileToCompleted(File file);

}

