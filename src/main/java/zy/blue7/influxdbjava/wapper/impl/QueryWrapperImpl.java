package zy.blue7.influxdbjava.wapper.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zy.blue7.influxdbjava.wapper.QueryWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryWrapperImpl implements QueryWrapper {
    /**
     * sql语句
     */
    StringBuffer sql = new StringBuffer();
    /**
     * 查询的字段集合
     */
    Set<String> selectFields = new HashSet<>();
    /**
     * 从那张表查询或者多张表
     */
    Set<String> selectFrom = new HashSet<>();
    /**
     * 查询条件
     */
    List<String> queryConditions = new ArrayList<>();

    /**
     * 分组查询的tagskey
     */
    Set<String> groupByTags = new HashSet<>();
    /**
     * 判断升序降序,默认asc
     */
    String orderByTime = "asc";
    /**
     * 分页的每一页的数量
     */
    int limitSize = -1;
    /**
     * series 的数量，即前 sLimitSize 个series
     */
    int sLimitSize = -1;

    /**
     * 分页查询的第几页（相对于 记录）
     */
    int offset = -1;
    /**
     * 分页查询的第几页（相对于series）
     */
    int sOffset = -1;

    String intoMeasurement = "";

    String fillStr = "";


    @Override
    public QueryWrapper select(String... sqlSelect) {
        for (String s : sqlSelect) {
            selectFields.add(s);
        }
        return this;
    }

    @Override
    public QueryWrapper into(String measurement) {
        if (measurement.equalsIgnoreCase("") || measurement == null) {
            this.intoMeasurement = "";
        } else {
            this.intoMeasurement = measurement;
        }
        return this;
    }

    @Override
    public QueryWrapper from(String... sqlFrom) {
        for (String s : sqlFrom) {
            selectFrom.add(s);
        }
        return this;
    }

    /**
     * tagvalue只支持字符串，要用单引号括起来
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public QueryWrapper eqTag(Object key, Object value) {
        if (value instanceof String) {
            queryConditions.add(key + "=" + "'" + value + "'");
        } else {
            throw new RuntimeException("where 子句中的tagvalue只支持字符串，传入的value值的类型是；" + value.getClass());
        }

        return this;
    }

    @Override
    public QueryWrapper neTag(Object key, Object value) {
        if (value instanceof String) {
            queryConditions.add(key + "!=" + "'" + value + "'");
        } else {
            throw new RuntimeException("where 子句中的tagvalue只支持字符串，传入的value值的类型是；" + value.getClass());
        }

        return this;
    }

    private void fieldProcess(Object key, Object value, String symbol) {
        /**
         * 数值类型就直接显示
         */
        if (value instanceof Number) {
            queryConditions.add(key + symbol + value);
        } else if (value instanceof String) {
            /**
             * 字符串类型要 加单引号
             */
            queryConditions.add(key + symbol + "'" + value + "'");
        } else if (value instanceof Boolean) {
            queryConditions.add(key + symbol + value);
        } else {
            throw new RuntimeException("fieldvalue 不支持的数据类型：" + value.getClass());
        }
    }

    /**
     * field只有四种类型 字符串，布尔型，浮点数和整数这些类型
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public QueryWrapper eqField(Object key, Object value) {
        String symbol = "=";
        this.fieldProcess(key, value, symbol);
        return this;
    }

    @Override
    public QueryWrapper neField(Object key, Object value) {
        String symbol = "!=";
        this.fieldProcess(key, value, symbol);
        return this;
    }


    @Override
    public QueryWrapper ltField(Object key, Object value) {
        String symbol = "<";
        this.fieldProcess(key, value, symbol);
        return this;
    }

    @Override
    public QueryWrapper gtField(Object key, Object value) {
        String symbol = ">";
        this.fieldProcess(key, value, symbol);
        return this;
    }

    @Override
    public QueryWrapper geField(Object key, Object value) {
        String symbol = ">=";
        this.fieldProcess(key, value, symbol);
        return this;
    }

    @Override
    public QueryWrapper leField(Object key, Object value) {
        String symbol = "<=";
        this.fieldProcess(key, value, symbol);
        return this;
    }

    @Override
    public QueryWrapper timeNow(String value) {
        /**
         * time > now() - 5m
         */
        this.queryConditions.add(" time > now() -"+value+" ");
        return this;
    }

    @Override
    public QueryWrapper groupBy(String... tagsKey) {
        for (String s : tagsKey) {
            groupByTags.add(s);
        }
        return this;
    }

    @Override
    public QueryWrapper groupByTime(String time) {
        groupByTags.add(" time(" + time + ")");
        return this;
    }

    @Override
    public QueryWrapper groupByTime(String time, String offsetTime) {
        groupByTags.add(" time(" + time + "," + offsetTime + ")");
        return this;
    }

    @Override
    public QueryWrapper fill(String fillStr) {
        if (fillStr.isEmpty() || fillStr.equalsIgnoreCase("") || fillStr == null) {
            this.fillStr = null;
        } else {
            this.fillStr = fillStr;
        }
        return this;
    }


    @Override
    public QueryWrapper orderByTime(String orderBy) {
        if (orderBy.equalsIgnoreCase("asc") || orderBy.equalsIgnoreCase("desc")) {
            this.orderByTime = orderBy;
        } else {
            this.orderByTime = null;
        }
        return this;
    }

    @Override
    public QueryWrapper limit(int size) {
        if (size > 0) {
            this.limitSize = size;
        } else {
            throw new RuntimeException("分页查询，每一页的数量不能为负数");
        }

        return this;
    }

    @Override
    public QueryWrapper sLimit(int size) {
        if (size > 0) {
            this.sLimitSize = size;
        } else {
            throw new RuntimeException("分页查询，每一页的数量不能为负数");
        }
        return this;
    }

    @Override
    public QueryWrapper limitAndSLimit(int lSize, int sLSize) {
        if (lSize > 0) {
            this.sLimitSize = lSize;
        } else {
            throw new RuntimeException("分页查询，每一页的数量不能为负数");
        }
        if (sLSize > 0) {
            this.sLimitSize = sLSize;
        } else {
            throw new RuntimeException("分页查询，每一页的数量不能为负数");
        }
        return this;
    }

    @Override
    public QueryWrapper offset(int pageNum) {
        if (pageNum > 0) {
            this.offset = pageNum;
        } else {
            throw new RuntimeException("分页查询，分页的数量不能为负数");
        }
        return this;
    }

    @Override
    public QueryWrapper sOffset(int pageNum) {
        if (pageNum > 0) {
            this.sOffset = pageNum;
        } else {
            throw new RuntimeException("分页查询，分页的数量不能为负数");
        }
        return this;
    }

    @Override
    public QueryWrapper or() {
        queryConditions.add(" or ");
        return this;
    }

    @Override
    public QueryWrapper and() {
        queryConditions.add(" and ");
        return this;
    }

    @Override
    public QueryWrapper selectLike(String... selectRegex) {
        for (String s : selectRegex) {
            selectFields.add("/" + s + "/");
        }
        return this;
    }

    @Override
    public QueryWrapper fromLike(String... fromRegex) {
        for (String s : fromRegex) {
            selectFrom.add("/" + s + "/");
        }
        return this;
    }

    @Override
    public QueryWrapper whereLike(Object key, String regex) {
        String symbol = "=~";
        queryConditions.add(key + symbol + "/" + regex + "/");
        return this;
    }

    @Override
    public QueryWrapper whereNotLike(Object key, String regex) {
        String symbol = "!~";
        queryConditions.add(key + symbol + "/" + regex + "/");
        return this;
    }

    @Override
    public QueryWrapper groupByLike(String... tagsRegex) {
        for (String s : tagsRegex) {
            groupByTags.add("/" + s + "/");
        }
        return this;
    }

    @Override
    public QueryWrapper count(String param) {
        selectFields.add(" count(" + param + ")");
        return this;
    }

    @Override
    public QueryWrapper countLike(String paramRegex) {
        selectFields.add(" count(/" + paramRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper distinct(String param) {
        selectFields.add(" distinct(" + param + ")");
        return this;
    }

    //    @Override
//    public QueryWrapper distinctLike(String paramRegex) {
//        selectFields.add(" distinct(/"+paramRegex+"/)");
//        return this;
//    }
    @Override
    public QueryWrapper countDistinct(String param) {
        selectFields.add("count(distinct(" + param + "))");
        return this;
    }


    @Override
    public QueryWrapper integral(String integralParam) {
        selectFields.add(" integral(" + integralParam + ")");
        return this;
    }

    @Override
    public QueryWrapper integral(String integralParam, String time) {
        selectFields.add(" integral(" + integralParam + "," + time + ")");
        return this;
    }

    @Override
    public QueryWrapper integralLike(String integralParamRegex) {
        selectFields.add(" integral(/" + integralParamRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper integralLike(String integralParamRegex, String time) {
        selectFields.add(" integral(/" + integralParamRegex + "/," + time + ")");
        return this;
    }

    @Override
    public QueryWrapper mean(String meanParam) {
        selectFields.add(" mean(" + meanParam + ")");
        return this;
    }

    @Override
    public QueryWrapper meanLike(String meanRegex) {
        selectFields.add(" mean(/" + meanRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper median(String medianParam) {
        selectFields.add(" median(" + medianParam + ")");
        return this;
    }

    @Override
    public QueryWrapper medianLike(String medianRegex) {
        selectFields.add(" median(/" + medianRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper mode(String modeParam) {
        selectFields.add(" mode(" + modeParam + ")");
        return this;
    }

    @Override
    public QueryWrapper modeLike(String modeRegex) {
        selectFields.add(" mode(/" + modeRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper spread(String spreadParam) {
        selectFields.add(" spread(" + spreadParam + ")");
        return this;
    }

    @Override
    public QueryWrapper spreadLike(String spreadParamRegex) {
        selectFields.add(" spread(/" + spreadParamRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper stdDev(String stdDevParam) {
        selectFields.add(" stddev(" + stdDevParam + ")");
        return this;
    }

    @Override
    public QueryWrapper stdDevLike(String stdDevRegex) {
        selectFields.add(" stddev(/" + stdDevRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper sum(String sumParam) {
        selectFields.add(" sum(" + sumParam + ")");
        return this;
    }

    @Override
    public QueryWrapper sumLike(String sumRegex) {
        selectFields.add(" sum(/" + sumRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper bottom(String fieldKey, int num) {
        selectFields.add(" bottom(" + fieldKey + "," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper bottom(String fieldKey, int num, String... tagsKey) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" bottom(");
        stringBuffer.append(fieldKey + ",");
        for (String s : tagsKey) {
            stringBuffer.append(s + ",");
        }
        stringBuffer.append(num);
        stringBuffer.append(")");

        selectFields.add(stringBuffer.toString());

        return this;
    }

    @Override
    public QueryWrapper first(String param) {
        selectFields.add(" first(" + param + ")");
        return this;
    }

    @Override
    public QueryWrapper firstLike(String regex) {
        selectFields.add(" first(/" + regex + "/)");
        return this;
    }

    @Override
    public QueryWrapper last(String param) {
        selectFields.add(" last(/" + param + "/)");
        return this;
    }

    @Override
    public QueryWrapper lastLike(String regex) {
        selectFields.add(" last(/" + regex + "/)");
        return this;
    }

    @Override
    public QueryWrapper max(String param) {
        selectFields.add(" max(" + param + ")");
        return this;
    }

    @Override
    public QueryWrapper maxLike(String paramRegex) {
        selectFields.add(" max(/" + paramRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper min(String param) {
        selectFields.add(" min(" + param + ")");
        return this;
    }

    @Override
    public QueryWrapper minLike(String paramRegex) {
        selectFields.add(" min(/" + paramRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper percentile(String fieldKey, int num) {
        selectFields.add(" percentile(" + fieldKey + "," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper percentileLike(String fieldKeyRegex, int num) {
        selectFields.add(" percentile(/" + fieldKeyRegex + "/," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper sample(String fieldKey, int num) {
        selectFields.add(" sample(" + fieldKey + "," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper sampleLike(String fieldKeyRegex, int num) {
        selectFields.add(" sample(/" + fieldKeyRegex + "/," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper top(String fieldKey, int num) {
        selectFields.add(" top(" + fieldKey + "," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper top(String fieldKey, int num, String... tagsKeys) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" top(");
        stringBuffer.append(fieldKey + ",");
        for (String s : tagsKeys) {
            stringBuffer.append(s + ",");
        }
        stringBuffer.append(num);
        stringBuffer.append(")");

        selectFields.add(stringBuffer.toString());
        return this;
    }



    @Override
    public QueryWrapper derivative(String param) {
        selectFields.add(" derivative(" + param + ")");
        return this;
    }

    @Override
    public QueryWrapper derivative(String param, String time) {
        selectFields.add(" derivative(" + param + "," + time + ")");
        return this;
    }

    @Override
    public QueryWrapper derivativeLike(String param) {
        selectFields.add(" derivative(/" + param + "/)");
        return this;
    }

    @Override
    public QueryWrapper derivativeLike(String param, String time) {
        selectFields.add(" derivative(/" + param + "/," + time + ")");
        return this;
    }

    @Override
    public QueryWrapper abs(String fieldKey) {
        selectFields.add(" ABS(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper acos(String fieldKey) {
        selectFields.add(" ACOS(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper cos(String fieldKey) {
        selectFields.add(" COS(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper cumulative_sum(String fieldKey) {
        selectFields.add(" CUMULATIVE_SUM(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper cumulative_sumLike(String regex) {
        selectFields.add(" CUMULATIVE_SUM(/" + regex +"/)");
        return this;
    }

    @Override
    public QueryWrapper asin(String fieldKey) {
        selectFields.add(" ASIN(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper sin(String fieldKey) {
        selectFields.add(" SIN(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper sqrt(String fieldKey) {
        selectFields.add(" SQRT(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper atan(String fieldKey) {
        selectFields.add(" ATAN(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper tan(String fieldKey) {
        selectFields.add(" TAN(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper atan2(String fieldKey1, String fieldKey2) {
        selectFields.add(" ATAN2(" + fieldKey1+" , "+fieldKey2 +")");
        return this;
    }

    @Override
    public QueryWrapper ceil(String fieldKey) {
        selectFields.add(" CEIL(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper floor(String fieldKey) {
        selectFields.add(" FLOOR(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper ln(String fieldKey) {
        selectFields.add(" LN(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper log(String fieldKey, String b) {
        selectFields.add(" LOG(" + fieldKey +" , "+b+")");
        return this;
    }

    @Override
    public QueryWrapper log2(String fieldKey) {
        selectFields.add(" LOG2(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper log10(String fieldKey) {
        selectFields.add(" LOG10(" + fieldKey +")");
        return this;
    }


    @Override
    public QueryWrapper moving_average(String fieldKey, String n) {
        selectFields.add(" MOVING_AVERAGE(" + fieldKey +" , "+n+")");
        return this;
    }

    @Override
    public QueryWrapper moving_averageLike(String regex, String n) {
        selectFields.add(" MOVING_AVERAGE(/" + regex +"/ , "+n+")");
        return this;
    }

    @Override
    public QueryWrapper non_negative_derivative(String fieldKey) {
        selectFields.add(" NON_NEGATIVE_DERIVATIVE(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper non_negative_derivativeLike(String regex) {
        selectFields.add(" NON_NEGATIVE_DERIVATIVE(/" + regex +"/)");
        return this;
    }

    @Override
    public QueryWrapper non_negative_derivative(String fieldKey, String unit) {
        selectFields.add(" NON_NEGATIVE_DERIVATIVE(" + fieldKey +" , "+unit+")");
        return this;
    }

    @Override
    public QueryWrapper non_negative_derivativeLike(String regex, String unit) {
        selectFields.add(" NON_NEGATIVE_DERIVATIVE(/" + regex +" , "+unit+"/)");
        return this;
    }

    @Override
    public QueryWrapper non_negative_difference(String fieldKey) {
        selectFields.add(" NON_NEGATIVE_DIFFERENCE(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper non_negative_differenceLike(String regex) {
        selectFields.add(" NON_NEGATIVE_DIFFERENCE(/" + regex +"/)");
        return this;
    }

    @Override
    public QueryWrapper pow(String fieldKey, String x) {
        selectFields.add(" POW(" + fieldKey+" , "+x +")");
        return this;
    }

    @Override
    public QueryWrapper round(String fieldKey) {
        selectFields.add(" ROUND(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper difference(String fieldKey) {
        selectFields.add(" DIFFERENCE(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper differenceLike(String regex) {
        selectFields.add(" DIFFERENCE(/" + regex +"/)");
        return this;
    }

    @Override
    public QueryWrapper elapsed(String fieldKey) {
        selectFields.add(" ELAPSED(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper elapsed(String fieldKey, String unit) {
        selectFields.add(" ELAPSED(" + fieldKey +" , "+unit+")");
        return this;
    }

    @Override
    public QueryWrapper elapsedLike(String regex) {
        selectFields.add(" ELAPSED(/" + regex +"/)");
        return this;
    }

    @Override
    public QueryWrapper elapsedLike(String regex, String unit) {
        selectFields.add(" ELAPSED(/" + regex +"/ , "+unit+")");
        return this;
    }

    @Override
    public QueryWrapper exp(String fieldKey) {
        selectFields.add(" EXP(" + fieldKey +")");
        return this;
    }

    @Override
    public QueryWrapper funToFun(String outFunName, String inFunName, String... params) {
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append(" "+outFunName+"(");
        stringBuffer.append(inFunName+"(");
        String s = String.join(",", params);
        stringBuffer.append(s);
        stringBuffer.append(")");
        stringBuffer.append(") ");

        String ss=stringBuffer.toString();
        selectFields.add(ss);
        return this;
    }

    @Override
    public String getSqlSelect() {
        sql.append("SELECT ");
        for (String s : selectFields) {
            if (s.equalsIgnoreCase((String) selectFields.toArray()[0])) {
                sql.append(" " + s + " ");
            } else {
                sql.append(" , " + s + " ");
            }
        }

        if (!intoMeasurement.equalsIgnoreCase("") && intoMeasurement != null) {
            sql.append(" into " + intoMeasurement);
        }

        sql.append(" FROM ");
        for (String s : selectFrom) {
            if (s.equalsIgnoreCase((String) selectFrom.toArray()[0])) {
                sql.append(" " + s + " ");
            } else {
                sql.append(" , " + s + " ");
            }
        }
        if (!queryConditions.isEmpty() && queryConditions.size() != 0) {
            sql.append(" where ");
            for (String s : queryConditions) {
                sql.append(" " + s + " ");
            }
        }
        if (!groupByTags.isEmpty() && groupByTags.size() != 0) {
            sql.append(" group by ");
            for (String s : groupByTags) {
                if (s.equalsIgnoreCase((String) groupByTags.toArray()[0])) {
                    sql.append(" " + s + " ");
                } else {
                    sql.append(" , " + s + " ");
                }
            }
        }
        if (!fillStr.isEmpty() && !fillStr.equalsIgnoreCase("") && fillStr != null) {
            sql.append(" fill(" + fillStr + ")  ");
        }
        if (!orderByTime.equalsIgnoreCase("") && orderByTime != null) {
            sql.append("order by time " + orderByTime);
        }
        if (limitSize > 0) {
            sql.append(" limit " + limitSize);
        }
        if (offset > 0) {
            sql.append(" offset " + offset);
        }
        if (sLimitSize > 0) {
            sql.append(" slimit " + sLimitSize);
        }
        if (sOffset > 0) {
            sql.append(" soffset " + sOffset);
        }

        String ss = this.getSql().toString().replaceAll("\\s+", " ");
        return ss;
    }
}
