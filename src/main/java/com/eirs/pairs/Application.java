package com.eirs.pairs;

import com.eirs.pairs.alerts.AlertServiceImpl;
import com.eirs.pairs.service.InvalidImeiServiceImpl;
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
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
@EnableEncryptableProperties
public class Application {

    private static final Logger log = LogManager.getLogger(Application.class);

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(Application.class, args);
        LocalDate date = LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        log.info("Auto Pairing Mode Processing for date:{}", date);
        context.getBean(RecordDateEdrProcessor.class).processEdr(date);
        context.getBean(AlertServiceImpl.class).emptyAlertQueue();
        try {
            TimeUnit.SECONDS.sleep(1);
            System.exit(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
