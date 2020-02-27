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
public class Config {

    @Value("${src.file}")
    private String srcFileName;

    public String getSrcFileName() {
        return srcFileName;
    }


}
