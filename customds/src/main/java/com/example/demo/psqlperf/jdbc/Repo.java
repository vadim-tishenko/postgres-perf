package com.example.demo.psqlperf.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class Repo {
    private final DataSource dataSource;

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
            log.error("", e);
        }

    }

    /**
     * http://pgpen.blogspot.com/2013/05/using-copy-in-your-jdbc-code.html
     * https://www.programcreek.com/java-api-examples/?api=org.postgresql.copy.CopyManager
     * https://jdbc.postgresql.org/documentation/publicapi/org/postgresql/copy/CopyManager.html
     * https://spacanowski.github.io/java/postgres/2016/06/04/postgres-java-copy/
     *
     * @param list
     */

    public void batchCopySave(List<TfcSensor> list) {
        log.info("start save: {}", list.size());
        final int batchSize = 200_000;
//        insert   into tfc_sensor(id_tr, gmt_event_time, n_num, val, gmt_sys_time)
        byte bytes[] = {};//a.getBytes();

        try (Connection connection = dataSource.getConnection()) {
            final CopyManager copyManager = new CopyManager((BaseConnection) connection);

            int count = 0;

            StringBuilder sb = new StringBuilder();

            for (TfcSensor t : list) {
                sb.append(t.getIdTr()).append(',');
                sb.append(t.getGmtEventTime()).append(',');
                sb.append(t.getnNum()).append(',');
                sb.append(t.getVal()).append(',');
                sb.append(t.getGmtSysTime()).append('\n');

                count++;
                if (count == batchSize) {
                    save(copyManager, sb);
                    sb = new StringBuilder();
                    count = 0;
                }
            }

            if (count > 0) {
                save(copyManager, sb);
            }

        } catch (SQLException e) {
            log.error("", e);
        }
    }

    private void save(CopyManager copyManager, StringBuilder sb) throws SQLException {
        byte[] bytes;
        log.info("start convert ty bytes");
        bytes = sb.toString().getBytes();
        log.info("start save: {}", bytes.length);
        CopyIn cpIn = copyManager.copyIn("COPY tfc_sensor(id_tr, gmt_event_time, n_num, val, gmt_sys_time) FROM STDIN WITH DELIMITER ','");
        cpIn.writeToCopy(bytes, 0, bytes.length);
        long res = cpIn.endCopy();
        log.info("finish: {}", res);
    }
}
