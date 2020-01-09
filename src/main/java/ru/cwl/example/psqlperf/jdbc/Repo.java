package ru.cwl.example.psqlperf.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class Repo {
    DataSource dataSource;

    public Repo() {
        /**
         * et the property databaseName. The settings for serverName, portNumber, user, and password
         *  * are optional. Note: these properties are declared in the superclass.
         */
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setDatabaseName("postgres");
        source.setServerNames(new String[]{"localhost"});
        source.setPortNumbers(new int[]{54321});
        source.setUser("postgres");
        source.setPassword("123");
        dataSource = source;

    }

    public void batchSave(List<TfcSensor> list) {

        log.info("start save: {}", list.size());
        String sql = "insert into tfc_sensor(id_tr, gmt_event_time, n_num, val, gmt_sys_time) values (?,?,?,?,?)";
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {


                final int batchSize = 500000;
                int count = 0;


                for (TfcSensor t : list) {
                    try {
                        ps.setInt(1, t.getIdTr());

                        ps.setLong(2, t.getGmtEventTime());
                        ps.setInt(3, t.getnNum());
                        ps.setFloat(4, t.getVal());
                        ps.setLong(5, t.getGmtSysTime());
                        ps.addBatch();
                        if (++count % batchSize == 0) {
                            ps.executeBatch();
                            connection.commit();
                            log.info("saved {}", count);
                        }
                    } catch (SQLException e) {
                        log.error("", e);
                    }
                }

                ps.executeBatch(); // insert remaining records
                connection.commit();
                log.info("save ok");
            } catch (SQLException e) {
                connection.rollback();
                log.error("err", e);
            }

        } catch (SQLException e) {
            log.error("",e);
        }

    }
}
