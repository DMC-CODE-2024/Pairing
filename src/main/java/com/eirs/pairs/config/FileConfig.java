package com.eirs.pairs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "files")
@Data
public class FileConfig {

    private String edrFilesFolder;
    private String edrMoveFolder;
    private String edrCompletedFolder;
    private String edrAggregateFolder;
}
