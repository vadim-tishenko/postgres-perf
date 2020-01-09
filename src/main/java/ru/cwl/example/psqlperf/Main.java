package ru.cwl.example.psqlperf;

import lombok.extern.slf4j.Slf4j;
import ru.cwl.example.psqlperf.jdbc.TfcSensor;

import java.util.List;

/***
  "c://tmp//tfc_sensor_1559001600.csv";

 */

@Slf4j
public class Main {
    public static void main(String[] args) {
        ReadCSVFromFile r = new ReadCSVFromFile();
        Config cfg = new Config();

        String fileName = cfg.getSrcFileName();

        List<TfcSensor> result = r.processInputFile(fileName);

        log.info("s:{}",result.size());
    }
}
