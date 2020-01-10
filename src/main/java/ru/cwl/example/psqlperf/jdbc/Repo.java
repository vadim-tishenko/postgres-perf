package ru.cwl.example.psqlperf.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
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
            log.error("", e);
        }

    }

    public void batchCopySave(List<TfcSensor> list) {
        log.info("start save: {}", list.size());
        final int batchSize = 200_000;
//        String sql = "insert into tfc_sensor(id_tr, gmt_event_time, n_num, val, gmt_sys_time) values (?,?,?,?,?)";
//        String a = "1,2,3,4,5\n2,3,4,5,6\n";
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
                    log.info("start convert ty bytes");
                    bytes = sb.toString().getBytes();
                    log.info("start save: {}", bytes.length);
                    CopyIn cpIn = copyManager.copyIn("COPY tfc_sensor(id_tr, gmt_event_time, n_num, val, gmt_sys_time) FROM STDIN WITH DELIMITER ','");
                    cpIn.writeToCopy(bytes, 0, bytes.length);
                    long res = cpIn.endCopy();
                    log.info("finish: {}", res);
                    sb = new StringBuilder();
                    count = 0;
                }
            }


   /*         final BufferedReader from = new BufferedReader(new FileReader("C:/Users/gord/Desktop/testdata.csv"));
            long rowsInserted = copyManager.copyIn("COPY table1 FROM STDIN (FORMAT csv, HEADER)", from);
            System.out.printf("%d row(s) inserted%n", rowsInserted);*/

        } catch (SQLException e) {
            log.error("", e);
        }
    }
}
