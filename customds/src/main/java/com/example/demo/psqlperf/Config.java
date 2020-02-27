package com.example.demo.psqlperf;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Configuration
//@PropertySource("classpath:application.yml")
public class Config {
//    private static final String SRC_FILE = "src.file";

    @Value("${src.file}")
    private String srcFileName;

//    public Config() {
//        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
//
//            Properties prop = new Properties();
//
//            if (input == null) {
//                log.error("Sorry, unable to find config.properties");
//                return;
//            }
//
//            //load a properties file from class path, inside static method
//            prop.load(input);
//
//            srcFileName = prop.getProperty(SRC_FILE);
//
//        } catch (IOException ex) {
//            log.error("",ex);
//        }
//    }

    public String getSrcFileName() {
        return srcFileName;
    }

//    public static void main(String[] args) {
//
//        Config cfg = new Config();
//        log.info(cfg.srcFileName);
//
//
//    }

}
