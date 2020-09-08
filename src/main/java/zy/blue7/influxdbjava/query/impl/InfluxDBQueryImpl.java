package zy.blue7.influxdbjava.query.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import zy.blue7.influxdbjava.query.InfluxDBQuery;
import zy.blue7.influxdbjava.utils.StringUtils;
import zy.blue7.influxdbjava.wapper.QueryWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfluxDBQueryImpl<T> implements InfluxDBQuery<T> {
    private InfluxDB influxDB;

    @Override
    public List<T> query(QueryWrapper queryWrapper, Class clazz) {

        if (queryWrapper == null) {
            throw new RuntimeException("QueryWrapper不能为空！");
        }
        if(clazz==null){
            throw new RuntimeException("请输入一个Java类");
        }


        String sql=queryWrapper.getSqlSelect();
        if(sql==null||sql.equalsIgnoreCase("")){
            throw new RuntimeException("没有sql语句，请通过函数生成正确的sql语句");
        }

        QueryResult queryResult = influxDB.query(new Query(sql));

        List<T> list = new ArrayList<>();

        if(queryResult.getResults()==null||queryResult.getResults().size()==0||queryResult.getResults().isEmpty()){
            throw new RuntimeException("抱歉查询不到数据，results为空");
        }
        List<QueryResult.Result> results = queryResult.getResults();

        for (QueryResult.Result result : results) {
            if(result.getSeries()==null||result.getSeries().size()==0||result.getSeries().isEmpty()){
                throw new RuntimeException("抱歉查询不到数据，series为空");
            }
            for (QueryResult.Series series : result.getSeries()) {
                List<String> columns = series.getColumns();
                Map<String, String> tags = series.getTags();
                List<List<Object>> values = series.getValues();
                String measurement = series.getName();
//                /**
//                 * 这里是获取传入T的类型
//                 */
//                Class clazz = t.getClass();


                for (List<Object> objects : values) {
                    /**
                     * 创建一个对象
                     */
                    T obj = null;
                    try {
                        obj = (T) clazz.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }


                    if (measurement != null && !measurement.equalsIgnoreCase("")) {
                        Method method = null;
                        /**
                         * 这里设置表名，对象中默认表名是 measurement
                         */

                        Class<?> type = null;
                        try {
                            Field declaredField = clazz.getDeclaredField("measurement");
                            type = declaredField.getType();
                            method = clazz.getMethod("set" + StringUtils.toUpperCaseFirstOne("measurement"), type);
                        } catch (NoSuchMethodException | NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        try {
                            method.invoke(obj, measurement);
                        } catch (Exception e) {
                            throw new RuntimeException("数据类型转换错误，时序数据库查出来的字段 " + measurement + " 的类型为：" + measurement.getClass() + " 而对应的Java对象的相应属性的类型为：" + type);
                        }
                    }


                    /**
                     * 这里是将tags封装到 Java对象，一个tags对应多个values
                     */
                    if (tags != null && !tags.isEmpty()) {
                        for (Map.Entry<String, String> entry : tags.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();

                            Method method = null;


                            Class<?> type = null;
                            try {
                                Field declaredField = clazz.getDeclaredField(key);
                                type = declaredField.getType();
                                method = clazz.getMethod("set" + StringUtils.toUpperCaseFirstOne(key), type);
                            } catch (NoSuchMethodException | NoSuchFieldException e) {
                                e.printStackTrace();
                            }

                            try {
                                method.invoke(obj, value);
                            } catch (Exception e) {
                                throw new RuntimeException("数据类型转换错误，时序数据库查出来的字段 " + key + " 的类型为：" + value.getClass() + " 而对应的Java对象的相应属性的类型为：" + type);
                            }
                        }
                    }


                    if(columns==null||columns.size()==0||columns.isEmpty()){
                        throw new RuntimeException("没有查询到数据，columns为空");
                    }

                    /**
                     * 这里是将返回的结果集中的数据columns封装起来，
                     */
                    for (int i = 0; i < columns.size(); i++) {
                        String name = columns.get(i);

                        Object value = objects.get(i);

                        Method method = null;
                        Class<?> type = null;
                        Field declaredField=null;
                        try {
                            declaredField = clazz.getDeclaredField(name);
                            Optional<Field> fieldOptional = Optional.ofNullable(declaredField);
                            if (fieldOptional.isPresent()) {
                                String fieldName = fieldOptional.map(o -> o.getName()).orElse("unknowen");

                                if (fieldName.equalsIgnoreCase("time")) {
                                    String time = value.toString();
                                    time = time.replace("Z", " UTC");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
                                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
                                    try {

                                        /**
                                         * 这里是因为有时候返回的 带毫秒的时间字符串，有时候返回的是不带毫秒的时间字符串
                                         */
                                        Date parse = null;
                                        try{
                                            parse = dateFormat.parse(time);
                                        }catch (Exception e){
                                            parse = dateFormat2.parse(time);
                                        }finally {

                                        }
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        value = df.format(parse);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            type = declaredField.getType();
                            method = clazz.getMethod("set" + StringUtils.toUpperCaseFirstOne(name), type);
                        } catch (NoSuchMethodException | NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        try {
                            type=declaredField.getType();
                            /**
                             * 这里是将返回的数据转换成Java对象中对应的数据，由于返回的都是double类型的数据，所以需要转换一下
                             */
                            if(value instanceof Double){
                                if(type.getSimpleName().equalsIgnoreCase("integer")){
                                    Integer a= Integer.parseInt(value.toString());
                                    method.invoke(obj,a);
                                }else if(type.getSimpleName().equalsIgnoreCase("float")){
                                    Float a= Float.parseFloat(value.toString());
                                    method.invoke(obj,a);
                                }else if(type.getSimpleName().equalsIgnoreCase("short")){
                                    Short a= Short.parseShort(value.toString());
                                    method.invoke(obj,a);
                                }else if(type.getSimpleName().equalsIgnoreCase("long")){
                                    Long a= Long.parseLong(value.toString());
                                    method.invoke(obj,a);
                                }
                            }else {
                                method.invoke(obj, value);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("数据类型转换错误，时序数据库查出来的字段 " + name + " 的类型为：" + value.getClass() + " 而对应的Java对象的相应属性的类型为：" + type);
                        }
                    }
                    list.add(obj);
                }
            }
        }
        return list;
    }

    @Override
    public Map<String, Object> queryMap(QueryWrapper queryWrapper) {

        Map<String, Object> queryMap = new HashMap<>();

        if (queryWrapper == null) {
            throw new RuntimeException("QueryWrapper不能为空！");
        }
        QueryResult queryResult = influxDB.query(new Query(queryWrapper.getSqlSelect()));


        if(queryResult.getResults()==null||queryResult.getResults().size()==0||queryResult.getResults().isEmpty()){
            throw new RuntimeException("抱歉查询不到数据，results为空");
        }

        for (QueryResult.Result result : queryResult.getResults()) {
            if(result.getSeries()==null||result.getSeries().size()==0||result.getSeries().isEmpty()){
                throw new RuntimeException("抱歉查询不到数据，series为空");
            }
            for (QueryResult.Series series : result.getSeries()) {
                List<String> columns = series.getColumns();
                Map<String, String> tags = series.getTags();
                List<List<Object>> values = series.getValues();

//                queryMap.putAll(tags);
                if (values.isEmpty() || values.size() == 0 || values == null) {
                    return queryMap;
                }

                /**
                 * 这里只查询行数据，如果多行，默认只获取第一行
                 */
                List<Object> value = values.get(0);

                if(columns==null||columns.size()==0||columns.isEmpty()){
                    throw new RuntimeException("没有查询到数据，columns为空");
                }

                for (int i = 0; i < columns.size(); i++) {

                    String time=columns.get(i);
                    if(value.get(i)==null){
                        throw new RuntimeException("对应的列名 没有数据值");
                    }
                    Object valueTime=value.get(i);
                    String strTime=valueTime.toString();
                    /**
                     * 转换时间格式
                     */
                    if(time.equalsIgnoreCase("time")){
                        strTime = strTime.replace("Z", " UTC");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
                        try {
                            /**
                             * 这里是因为有时候返回的 带毫秒的时间字符串，有时候返回的是不带毫秒的时间字符串
                             */
                            Date parse = null;
                            try{
                                parse = dateFormat.parse(strTime);
                            }catch (Exception e){
                                parse = dateFormat2.parse(strTime);
                            }finally {

                            }

                            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            strTime = df.format(parse);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        queryMap.put(time, strTime);
                    }else {
                        queryMap.put(time, value.get(i));
                    }
                }
            }
        }
        return queryMap;
    }

    @Override
    public List<Map<String, Object>> queryMaps(QueryWrapper queryWrapper) {
        List<Map<String, Object>> queryMaps = new ArrayList<Map<String, Object>>();

        if (queryWrapper == null) {
            throw new RuntimeException("QueryWrapper不能为空！");
        }
        QueryResult queryResult = influxDB.query(new Query(queryWrapper.getSqlSelect()));

        if(queryResult.getResults()==null||queryResult.getResults().size()==0||queryResult.getResults().isEmpty()){
            throw new RuntimeException("抱歉查询不到数据，results为空");
        }
        for (QueryResult.Result result : queryResult.getResults()) {
            if(result.getSeries()==null||result.getSeries().size()==0||result.getSeries().isEmpty()){
                throw new RuntimeException("抱歉查询不到数据，series为空");
            }
            for (QueryResult.Series series : result.getSeries()) {
                List<String> columns = series.getColumns();
                Map<String, String> tags = series.getTags();
                List<List<Object>> values = series.getValues();

//                queryMap.putAll(tags);
                if (values.isEmpty() || values.size() == 0 || values == null) {
                    return queryMaps;
                }
                if(columns==null||columns.size()==0||columns.isEmpty()){
                    throw new RuntimeException("没有查询到数据，columns为空");
                }
                for (List<Object> value : values) {
                    Map<String, Object> queryMap = new HashMap<>();
                    if(value==null||value.isEmpty()||value.size()==0){
                        throw new RuntimeException("没有获取到数据，value为空");
                    }
                    for (int i = 0; i < columns.size(); i++) {
                        String time=columns.get(i);
                        if(value.get(i)==null){
                            throw new RuntimeException("对应的列名 没有数据值");
                        }
                        Object valueTime=value.get(i);
                        String strTime=valueTime.toString();
                        /**
                         * 转换时间格式
                         */
                        if(time.equalsIgnoreCase("time")){
                            strTime = strTime.replace("Z", " UTC");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
                            try {
                                /**
                                 * 这里是因为有时候返回的 带毫秒的时间字符串，有时候返回的是不带毫秒的时间字符串
                                 */
                                Date parse = null;
                                try{
                                    parse = dateFormat.parse(strTime);
                                }catch (Exception e){
                                    parse = dateFormat2.parse(strTime);
                                }finally {

                                }

                                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                strTime = df.format(parse);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            queryMap.put(time, strTime);
                        }else {
                            queryMap.put(time, value.get(i));
                        }
                    }

                    queryMaps.add(queryMap);
                }

            }
        }
        return queryMaps;
    }

    @Override
    public void setInfluxDB(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }
}
