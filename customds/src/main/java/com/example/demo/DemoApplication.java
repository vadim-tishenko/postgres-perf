package com.example.demo;

import com.example.demo.psqlperf.Config;
import com.example.demo.psqlperf.ReadCSVFromFile;
import com.example.demo.psqlperf.jdbc.Repo;
import com.example.demo.psqlperf.jdbc.TfcSensor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
        DataSource dataSource = ctx.getBean(DataSource.class);

                ReadCSVFromFile r = new ReadCSVFromFile();
        Config cfg = ctx.getBean(Config.class);

        String fileName = cfg.getSrcFileName();

        List<TfcSensor> result = r.processInputFile(fileName);

        log.info("s:{}",result.size());
        List<TfcSensor> l2 = result.subList(0, 15_000_000);
        Repo repo=ctx.getBean(Repo.class);
        repo.batchCopySave(l2);


//        try (Connection connection = dataSource.getConnection()) {
//            System.out.println(connection);
//            final CopyManager copyManager = new CopyManager((BaseConnection) connection);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

}
