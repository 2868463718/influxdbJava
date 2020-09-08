package zy.blue7.influxdbjava.wapper;

import org.influxdb.InfluxDB;

public interface InsertWrapper<T> {
    void save(T t) throws Exception;

    void setInfluxDB(InfluxDB influxDB);
}
