package zy.blue7.influxdbjava.start;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zy.blue7.influxdbjava.exception.NullDataBaseException;
import zy.blue7.influxdbjava.exception.NullPasswordException;
import zy.blue7.influxdbjava.exception.NullUrlException;
import zy.blue7.influxdbjava.exception.NullUserNameException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfluxDBAutoConfiguration {

    @Value("${spring.influx.url}")
    private String influxDBUrl;

    @Value("${spring.influx.database}")
    private String influxDBDatabase;

    @Value("${spring.influx.user}")
    private String influxDBUser;

    @Value("${spring.influx.password}")
    private String influxDBPassword;

    @Value("${spring.influx.retentionName}")
    private String influxDBRetentionName;

    @Value("${spring.influx.retentionTime}")
    private String influxDBRetentionTime;


    public InfluxDB start() throws Exception {

        if (influxDBUrl == null || influxDBUrl.equals("")) {
            throw new NullUrlException("连接数据库的地址不能为空，即influx.url不可以为空");
        }
        if (influxDBUser == null || influxDBUser.equals("")) {
            throw new NullUserNameException("数据库用户名不能为空，即influx.user不可以为空");
        }
        if (influxDBPassword == null || influxDBPassword.equals("")) {
            throw new NullPasswordException("数据库密码不能为空，即influx.password不可以为空");
        }
        InfluxDB influxDB = InfluxDBFactory.connect(influxDBUrl, influxDBUser, influxDBPassword);


        /**
         * 这个数据库名字必须有
         */
        if (influxDBDatabase == null || influxDBDatabase.equals("")) {
            throw new NullDataBaseException("数据库名不能为空，即influx.database不可以为空");
        }
        /**
         * 如果有database数据库，则不创建，如果没有，就创建
         */
        QueryResult show_databases = influxDB.query(new Query("show databases"));
        if (show_databases != null) {
            List<QueryResult.Result> results = show_databases.getResults();
            if (results != null && results.size() != 0 && !results.isEmpty()) {
                QueryResult.Result result = results.get(0);
                List<QueryResult.Series> series = result.getSeries();
                if (series != null && series.size() != 0 && !series.isEmpty()) {
                    QueryResult.Series series1 = series.get(0);
                    List<List<Object>> values = series1.getValues();
                    List<String> databases = new ArrayList<>();
                    if (values != null && values.size() != 0 && !values.isEmpty()) {
                        for (List<Object> value : values) {
                            if (value != null && value.size() != 0 && !value.isEmpty()) {
                                databases.add((String) value.get(0));
                            }
                        }
                    }

                    if (!databases.contains(influxDBDatabase)) {
                        influxDB.query(new Query("create database " + influxDBDatabase));
                    }
                }

            }
        }
        influxDB.setDatabase(influxDBDatabase);

        /**
         * 存储策略可有可无，没有设置就是默认的
         */

        if (influxDBRetentionName != null && !influxDBRetentionName.equals("")) {

            /**
             * 如果没有设置 存储策略时间，就默认一天
             */
            if (influxDBRetentionTime != null && !influxDBRetentionTime.equals("")) {
                influxDB.query(new Query("CREATE RETENTION POLICY " + influxDBRetentionName
                        + " ON " + influxDBDatabase + " DURATION " + influxDBRetentionTime + " REPLICATION 1 DEFAULT"));
            } else {
                influxDB.query(new Query("CREATE RETENTION POLICY " + influxDBRetentionName
                        + " ON " + influxDBDatabase + " DURATION 1d REPLICATION 1 DEFAULT"));
            }
            influxDB.setRetentionPolicy(influxDBRetentionTime);
        }
        return influxDB;
    }
}
