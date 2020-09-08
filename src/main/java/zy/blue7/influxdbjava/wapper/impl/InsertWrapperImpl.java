package zy.blue7.influxdbjava.wapper.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import zy.blue7.influxdbjava.utils.StringUtils;
import zy.blue7.influxdbjava.wapper.InsertWrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertWrapperImpl<T> implements InsertWrapper<T> {

    private InfluxDB influxDB;
    private List<String> tags = new ArrayList<>();
    private Map<String, String> fields = new HashMap<>();
    private String measurement = "";

    public InsertWrapperImpl(InfluxDB influxDB) {
        this.influxDB = influxDB;
        this.getFieldsAndTags(influxDB);
    }

    private void getFieldsAndTags(InfluxDB influxDB) {
        QueryResult show_tag_keys = influxDB.query(new Query("show tag keys"));
        QueryResult show_field_keys = influxDB.query(new Query("show field keys"));

        if (show_tag_keys.getResults().size() != 0 && show_tag_keys.getResults() != null && !show_tag_keys.getResults().isEmpty()) {
            QueryResult.Result result = show_tag_keys.getResults().get(0);
            List<QueryResult.Series> series = result.getSeries();
            if (series != null && series.size() != 0 && !series.isEmpty()) {
                QueryResult.Series series1 = series.get(0);
                List<List<Object>> values = series1.getValues();
                if (values != null && values.size() != 0 && !values.isEmpty()) {
                    for (List<Object> value : values) {
                        /**
                         * 有两列，第一列是 key名字，第二列是类型，
                         */
                        tags.add((String) value.get(0));
                    }
                } else {
                    throw new RuntimeException("values为空，没有找到tags");
                }
            } else {
                throw new RuntimeException("series为空");
            }
        } else {
            throw new RuntimeException("results为空");
        }

        if (show_field_keys.getResults().size() != 0 && show_field_keys.getResults() != null && !show_field_keys.getResults().isEmpty()) {
            QueryResult.Result result = show_field_keys.getResults().get(0);
            List<QueryResult.Series> series = result.getSeries();
            if (series != null && series.size() != 0 && !series.isEmpty()) {
                QueryResult.Series series1 = series.get(0);
                List<List<Object>> values = series1.getValues();
                if (values != null && values.size() != 0 && !values.isEmpty()) {
                    for (List<Object> value : values) {
                        /**
                         * 有两列，第一列是 key名字，第二列是类型，
                         */
                        fields.put((String) value.get(0), (String) value.get(1));
                    }
                } else {
                    throw new RuntimeException("values为空，没有找到tags");
                }
            } else {
                throw new RuntimeException("series为空");
            }
        } else {
            throw new RuntimeException("results为空");
        }
    }

    public void setInfluxDB(InfluxDB influxDB) {
        this.influxDB = influxDB;
        this.getFieldsAndTags(influxDB);
    }

    @Override
    public void save(T t) throws Exception {
        Map<Map<String, String>, Object> fieldsMap = new HashMap<>();
        Map<String, String> tagsMap = new HashMap<>();

        Class<?> clazz = t.getClass();

        /**
         * 如果没有measurement没有数据，按道理来说可能性不大，这一步有点不太肯定，无法判断tags和fields
         */
        if (fields == null || fields.size() == 0 || fields.isEmpty()) {
            throw new RuntimeException("抱歉，数据库没有数据，估计也没有那个表，请先录入一些数据");
        } else {
            for (String s : tags) {
                Method method = null;
                method = clazz.getMethod("get" + StringUtils.toUpperCaseFirstOne(s));
                try {
                    String ss = (String) method.invoke(t, null);
                    tagsMap.put(s, ss);
                } catch (Exception e) {
                    throw new RuntimeException("数据类型转换错误，时序数据库查出来的字段 " + "" + " 的类型为：" + " 而对应的Java对象的相应属性的类型为：" + "");
                }
            }
            //------------------------------------------------------------------
            /**
             * 设置measurement 值
             */
            Method method1 = null;
            method1 = clazz.getMethod("get" + StringUtils.toUpperCaseFirstOne("measurement"));
            try {
                measurement = (String) method1.invoke(t, null);

            } catch (Exception e) {
                throw new RuntimeException("数据类型转换错误，时序数据库查出来的字段 " + "" + " 的类型为：" + " 而对应的Java对象的相应属性的类型为：" + "");
            }

            //--------------------------------------------------------------------
            for (Map.Entry entry : fields.entrySet()) {
                String s = (String) entry.getKey();
                String type = (String) entry.getValue();
                Method method = null;
                if (type.equalsIgnoreCase("boolean")) {
                    method = clazz.getMethod("is" + StringUtils.toUpperCaseFirstOne(s));
                } else {
                    method = clazz.getMethod("get" + StringUtils.toUpperCaseFirstOne(s));
                }

                try {
                    Object ss = method.invoke(t, null);
                    Map<String, String> fieldMap = new HashMap<>();
                    fieldMap.put(s, type);
                    fieldsMap.put(fieldMap, ss);
                } catch (Exception e) {
                    throw new RuntimeException("数据类型转换错误，时序数据库查出来的字段 " + "" + " 的类型为：" + " 而对应的Java对象的相应属性的类型为：" + "");
                }
            }

        }

        Point.Builder builder = Point.measurement(measurement).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).tag(tagsMap);

        for (Map.Entry entry : fieldsMap.entrySet()) {
            Map<String, String> fieldMap = (Map<String, String>) entry.getKey();
            Object value = entry.getValue();

            String field = (String) fieldMap.keySet().toArray()[0];
            String type = fieldMap.get(field);

            if (type.equalsIgnoreCase("int")) {
                builder.addField(field, Integer.parseInt(value.toString()));
            } else if (type.equalsIgnoreCase("float")) {
                builder.addField(field, Float.parseFloat(value.toString()));
            } else if (type.equalsIgnoreCase("double")) {
                builder.addField(field, Double.parseDouble(value.toString()));
            } else if (type.equalsIgnoreCase("boolean")) {
                builder.addField(field, Boolean.parseBoolean(value.toString()));
            } else if (type.equalsIgnoreCase("string")) {
                builder.addField(field, value.toString());
            } else if (type.equalsIgnoreCase("long")) {
                builder.addField(field, Long.parseLong(value.toString()));
            } else if (type.equalsIgnoreCase("short")) {
                builder.addField(field, Short.parseShort(value.toString()));
            } else if (type.equalsIgnoreCase("Number")) {
                builder.addField(field, (Number) value);
            } else {
                throw new RuntimeException("不支持的数据类型");
            }
        }
        Point point = builder.build();
        influxDB.write(point);
    }
}
