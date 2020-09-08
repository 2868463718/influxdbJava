package zy.blue7.influxdbjava.query;

import org.influxdb.InfluxDB;
import zy.blue7.influxdbjava.wapper.QueryWrapper;

import java.util.List;
import java.util.Map;

public interface InfluxDBQuery<T> {


    List<T> query(QueryWrapper queryWrapper, Class clazz);

    Map<String,Object> queryMap(QueryWrapper queryWrapper);
    List<Map<String,Object>> queryMaps(QueryWrapper queryWrapper);
    void setInfluxDB(InfluxDB influxDB);
}
