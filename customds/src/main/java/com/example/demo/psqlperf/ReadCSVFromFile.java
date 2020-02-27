package com.example.demo.psqlperf;


import lombok.extern.slf4j.Slf4j;
import com.example.demo.psqlperf.jdbc.TfcSensor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ReadCSVFromFile {

    public List<TfcSensor> processInputFile(String inputFilePath) {
        List<TfcSensor> inputList = new ArrayList<>();
        log.info("0");
        File inputF = new File(inputFilePath);
        try (InputStream inputFS = new FileInputStream(inputF)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
            // skip the header of the csv
            Stream<String> skip = br.lines().skip(1).limit(1_000_000L * 15);
            log.info("1");
            inputList = skip.map(mapToItem).collect(Collectors.toList());
            log.info("2");
            br.close();
        } catch ( IOException e ) {
            log.error("",e);
        }
        return inputList;
    }


    long count = 0;
    //"ID_TR","GMTEVENTTIME","IS_LOCKED","ID_SENSOR","VAL","GMTSYSTIME","ID_SENSOR_TARGET"
    private Function<String, TfcSensor> mapToItem = (line) -> {
        final String COMMA = ",";
        String[] p = line.split(COMMA);// a CSV has comma separated lines
        TfcSensor item = new TfcSensor();
        count++;
        if (count % 1000000L == 0L) {
            log.info("{}",count);
        }
        item.setIdTr(Integer.parseInt(p[0]));
        item.setGmtEventTime(Long.parseLong(p[1]));
        item.setnNum(Integer.parseInt(p[3]));
        item.setVal(Float.parseFloat(p[4]));
        item.setGmtSysTime(Long.parseLong(p[5]));

        return item;
    };

}
