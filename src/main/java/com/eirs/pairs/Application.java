package com.eirs.pairs;

import com.eirs.pairs.service.RecordDateEdrProcessor;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableScheduling
@EnableEncryptableProperties
public class Application {

    private static final Logger log = LogManager.getLogger(Application.class);

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(Application.class, args);
        if (args == null || args.length == 0) {
            log.info("Up as Manual Pairing Mode");
        } else {
            LocalDate date = LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            log.info("Auto Pairing Mode Processing for date:{}", date);
            context.getBean(RecordDateEdrProcessor.class).processEdr(date);
            System.exit(0);
        }
    }

}
