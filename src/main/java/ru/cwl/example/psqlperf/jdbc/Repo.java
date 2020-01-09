package ru.cwl.example.psqlperf.jdbc;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Repo {
    DataSource dataSource;

    public void batchSave( List<TfcSensor> list) {

        String sql = "insert into tfc_sensor(id_tr, gmt_event_time, n_num, val, gmt_sys_time) values (?,?,?,?,?)";
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {


            final int batchSize = 1000;
            int count = 0;


            for (TfcSensor t : list) {
                try {
                    ps.setInt(1, t.getIdTr());

                    ps.setLong(2, t.getGmtEventTime());
                    ps.setInt(3, t.getnNum());
                    ps.setFloat(4, t.getVal());
                    ps.setLong(5, t.getGmtSysTime());
                    if (++count % batchSize == 0) {
                        ps.executeBatch();
                    }
                } catch (SQLException e) {
                    log.error("",e);
                }
            }


            ps.executeBatch(); // insert remaining records

        } catch (SQLException e) {
            log.error("",e);
        }
    }
}
