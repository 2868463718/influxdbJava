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
        if(sqlFrom==null||sqlFrom.length==0||sqlFrom.equals(null)){
            throw new RuntimeException("要查询的表不能为空，即from函数的参数不能为空");
        }
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
    public QueryWrapper eqTag(String key, String value) {
        if (key == null || key.equalsIgnoreCase("")) {
            throw new RuntimeException("tag的 key不可以为空");
        }
        if (value == null || value.equalsIgnoreCase("")) {
            throw new RuntimeException("tag的 value不可以为空");
        }
        queryConditions.add(key + "=" + "'" + value + "'");
        return this;
    }

    @Override
    public QueryWrapper neTag(String key, String value) {
        if (key == null || key.equalsIgnoreCase("")) {
            throw new RuntimeException("tag的 key不可以为空");
        }
        if (value == null || value.equalsIgnoreCase("")) {
            throw new RuntimeException("tag的 value不可以为空");
        }
        queryConditions.add(key + "!=" + "'" + value + "'");

        return this;
    }

    private void fieldProcess(String key, Object value, String symbol) {
        if (key == null || key.equalsIgnoreCase("")) {
            throw new RuntimeException("field的 key不可以为空");
        }
        if (value == null ) {
            throw new RuntimeException("field的 value不可以为空");
        }

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
    public QueryWrapper eqField(String key, Object value) {

        String symbol = "=";
        this.fieldProcess(key, value, symbol);
        return this;
    }

    @Override
    public QueryWrapper neField(String key, Object value) {
        String symbol = "!=";
        this.fieldProcess(key, value, symbol);
        return this;
    }


    @Override
    public QueryWrapper ltField(String key, Object value) {
        String symbol = "<";
        this.fieldProcess(key, value, symbol);
        return this;
    }

    @Override
    public QueryWrapper gtField(String key, Object value) {
        String symbol = ">";
        this.fieldProcess(key, value, symbol);
        return this;
    }

    @Override
    public QueryWrapper geField(String key, Object value) {
        String symbol = ">=";
        this.fieldProcess(key, value, symbol);
        return this;
    }

    @Override
    public QueryWrapper leField(String key, Object value) {
        String symbol = "<=";
        this.fieldProcess(key, value, symbol);
        return this;
    }

    @Override
    public QueryWrapper timeNow(String value) {
        /**
         * time > now() - 5m
         */
        this.queryConditions.add(" time > now() -" + value + " ");
        return this;
    }

    @Override
    public QueryWrapper groupBy(String... tagsKey) {
        if(tagsKey==null||tagsKey.length==0||tagsKey.equals(null)){
            throw new RuntimeException("分组groupby后面不可以不跟参数，即groupBy函数的参数不能为空");
        }

        for (String s : tagsKey) {
            groupByTags.add(s);
        }
        return this;
    }

    @Override
    public QueryWrapper groupByTime(String time) {
        if(time==null||time.equalsIgnoreCase("")||time.isEmpty()){
            throw new RuntimeException("groupByTime函数的参数为空");
        }
        groupByTags.add(" time(" + time + ")");
        return this;
    }

    @Override
    public QueryWrapper groupByTime(String time, String offsetTime) {
        if(time==null||time.equalsIgnoreCase("")||time.isEmpty()){
            throw new RuntimeException("groupByTime函数的参数为空");
        }
        if(offsetTime==null||offsetTime.equalsIgnoreCase("")||offsetTime.isEmpty()){
            throw new RuntimeException("groupByTime函数的参数为空");
        }
        groupByTags.add(" time(" + time + "," + offsetTime + ")");
        return this;
    }

    @Override
    public QueryWrapper fill(String fillStr) {
        if (fillStr == null||fillStr.isEmpty() || fillStr.equalsIgnoreCase("") ) {
            this.fillStr = null;
        } else {
            this.fillStr = fillStr;
        }
        return this;
    }


    @Override
    public QueryWrapper orderByTime(String orderBy) {
        if(orderBy==null||orderBy.isEmpty()||orderBy.equalsIgnoreCase("")){
            throw new RuntimeException("");
        }
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
        if(selectRegex==null||selectRegex.equals(null)||selectRegex.length==0){
            throw new RuntimeException("selectLike函数参数不能为空");
        }
        for (String s : selectRegex) {
            selectFields.add("/" + s + "/");
        }
        return this;
    }

    @Override
    public QueryWrapper fromLike(String... fromRegex) {
        if(fromRegex==null||fromRegex.equals(null)||fromRegex.length==0){
            throw new RuntimeException("fromLike函数参数不能为空");
        }
        for (String s : fromRegex) {
            selectFrom.add("/" + s + "/");
        }
        return this;
    }

    @Override
    public QueryWrapper whereLike(String key, String regex) {
        String symbol = "=~";
        queryConditions.add(key + symbol + "/" + regex + "/");
        return this;
    }

    @Override
    public QueryWrapper whereNotLike(String key, String regex) {
        String symbol = "!~";
        queryConditions.add(key + symbol + "/" + regex + "/");
        return this;
    }

    @Override
    public QueryWrapper groupByLike(String... tagsRegex) {
        if(tagsRegex==null||tagsRegex.equals(null)||tagsRegex.length==0){
            throw new RuntimeException("groupByLike函数参数不能为空");
        }
        for (String s : tagsRegex) {
            groupByTags.add("/" + s + "/");
        }
        return this;
    }

    @Override
    public QueryWrapper count(String param) {
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("count函数参数为空");
        }
        selectFields.add(" count(" + param + ")");
        return this;
    }

    @Override
    public QueryWrapper countLike(String paramRegex) {
        if(paramRegex==null||paramRegex.equalsIgnoreCase("")||paramRegex.isEmpty()){
            throw new RuntimeException("countLike函数参数为空");
        }
        selectFields.add(" count(/" + paramRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper distinct(String param) {
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("distinct函数参数为空");
        }
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
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("countDistinct函数参数为空");
        }
        selectFields.add("count(distinct(" + param + "))");
        return this;
    }


    @Override
    public QueryWrapper integral(String integralParam) {
        if(integralParam==null||integralParam.equalsIgnoreCase("")||integralParam.isEmpty()){
            throw new RuntimeException("integral函数参数为空");
        }
        selectFields.add(" integral(" + integralParam + ")");
        return this;
    }

    @Override
    public QueryWrapper integral(String integralParam, String time) {
        if(integralParam==null||integralParam.equalsIgnoreCase("")||integralParam.isEmpty()){
            throw new RuntimeException("integral函数参数integralParam为空");
        }
        if(time==null||time.equalsIgnoreCase("")||time.isEmpty()){
            throw new RuntimeException("integral函数参数time为空");
        }
        selectFields.add(" integral(" + integralParam + "," + time + ")");
        return this;
    }

    @Override
    public QueryWrapper integralLike(String integralParamRegex) {
        if(integralParamRegex==null||integralParamRegex.equalsIgnoreCase("")||integralParamRegex.isEmpty()){
            throw new RuntimeException("integralLike函数参数为空");
        }
        selectFields.add(" integral(/" + integralParamRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper integralLike(String integralParamRegex, String time) {
        if(integralParamRegex==null||integralParamRegex.equalsIgnoreCase("")||integralParamRegex.isEmpty()){
            throw new RuntimeException("integralLike函数参数为空");
        }
        if(time==null||time.equalsIgnoreCase("")||time.isEmpty()){
            throw new RuntimeException("integralLike函数参数为空");
        }

        selectFields.add(" integral(/" + integralParamRegex + "/," + time + ")");
        return this;
    }

    @Override
    public QueryWrapper mean(String meanParam) {
        if(meanParam==null||meanParam.equalsIgnoreCase("")||meanParam.isEmpty()){
            throw new RuntimeException("mean函数参数为空");
        }
        selectFields.add(" mean(" + meanParam + ")");
        return this;
    }

    @Override
    public QueryWrapper meanLike(String meanRegex) {
        if(meanRegex==null||meanRegex.equalsIgnoreCase("")||meanRegex.isEmpty()){
            throw new RuntimeException("meanLike函数参数为空");
        }
        selectFields.add(" mean(/" + meanRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper median(String medianParam) {
        if(medianParam==null||medianParam.equalsIgnoreCase("")||medianParam.isEmpty()){
            throw new RuntimeException("median函数参数为空");
        }
        selectFields.add(" median(" + medianParam + ")");
        return this;
    }

    @Override
    public QueryWrapper medianLike(String medianRegex) {
        if(medianRegex==null||medianRegex.equalsIgnoreCase("")||medianRegex.isEmpty()){
            throw new RuntimeException("medianLike函数参数为空");
        }
        selectFields.add(" median(/" + medianRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper mode(String modeParam) {
        if(modeParam==null||modeParam.equalsIgnoreCase("")||modeParam.isEmpty()){
            throw new RuntimeException("mode函数参数为空");
        }
        selectFields.add(" mode(" + modeParam + ")");
        return this;
    }

    @Override
    public QueryWrapper modeLike(String modeRegex) {
        if(modeRegex==null||modeRegex.equalsIgnoreCase("")||modeRegex.isEmpty()){
            throw new RuntimeException("modeLike函数参数为空");
        }
        selectFields.add(" mode(/" + modeRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper spread(String spreadParam) {
        if(spreadParam==null||spreadParam.equalsIgnoreCase("")||spreadParam.isEmpty()){
            throw new RuntimeException("spread函数参数为空");
        }
        selectFields.add(" spread(" + spreadParam + ")");
        return this;
    }

    @Override
    public QueryWrapper spreadLike(String spreadParamRegex) {
        if(spreadParamRegex==null||spreadParamRegex.equalsIgnoreCase("")||spreadParamRegex.isEmpty()){
            throw new RuntimeException("spreadLike函数参数为空");
        }
        selectFields.add(" spread(/" + spreadParamRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper stdDev(String stdDevParam) {
        if(stdDevParam==null||stdDevParam.equalsIgnoreCase("")||stdDevParam.isEmpty()){
            throw new RuntimeException("stdDev函数参数为空");
        }
        selectFields.add(" stddev(" + stdDevParam + ")");
        return this;
    }

    @Override
    public QueryWrapper stdDevLike(String stdDevRegex) {
        if(stdDevRegex==null||stdDevRegex.equalsIgnoreCase("")||stdDevRegex.isEmpty()){
            throw new RuntimeException("stdDevLike函数参数为空");
        }
        selectFields.add(" stddev(/" + stdDevRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper sum(String sumParam) {
        if(sumParam==null||sumParam.equalsIgnoreCase("")||sumParam.isEmpty()){
            throw new RuntimeException("sum函数参数为空");
        }
        selectFields.add(" sum(" + sumParam + ")");
        return this;
    }

    @Override
    public QueryWrapper sumLike(String sumRegex) {
        if(sumRegex==null||sumRegex.equalsIgnoreCase("")||sumRegex.isEmpty()){
            throw new RuntimeException("sumLike函数参数为空");
        }
        selectFields.add(" sum(/" + sumRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper bottom(String fieldKey, int num) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("bottom函数参数为空");
        }
        selectFields.add(" bottom(" + fieldKey + "," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper bottom(String fieldKey, int num, String... tagsKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("bottom函数参数为空");
        }
        if(tagsKey==null||tagsKey.length==0||tagsKey.equals(null)){
            throw new RuntimeException("bottom函数参数为空");
        }


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
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("first函数参数为空");
        }
        selectFields.add(" first(" + param + ")");
        return this;
    }

    @Override
    public QueryWrapper firstLike(String regex) {
        if(regex==null||regex.equalsIgnoreCase("")||regex.isEmpty()){
            throw new RuntimeException("firstLike函数参数为空");
        }
        selectFields.add(" first(/" + regex + "/)");
        return this;
    }

    @Override
    public QueryWrapper last(String param) {
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("last函数参数为空");
        }
        selectFields.add(" last(/" + param + "/)");
        return this;
    }

    @Override
    public QueryWrapper lastLike(String regex) {
        if(regex==null||regex.equalsIgnoreCase("")||regex.isEmpty()){
            throw new RuntimeException("lastLike函数参数为空");
        }
        selectFields.add(" last(/" + regex + "/)");
        return this;
    }

    @Override
    public QueryWrapper max(String param) {
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("max函数参数为空");
        }
        selectFields.add(" max(" + param + ")");
        return this;
    }

    @Override
    public QueryWrapper maxLike(String paramRegex) {
        if(paramRegex==null||paramRegex.equalsIgnoreCase("")||paramRegex.isEmpty()){
            throw new RuntimeException("maxLike函数参数为空");
        }
        selectFields.add(" max(/" + paramRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper min(String param) {
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("min函数参数为空");
        }
        selectFields.add(" min(" + param + ")");
        return this;
    }

    @Override
    public QueryWrapper minLike(String paramRegex) {
        if(paramRegex==null||paramRegex.equalsIgnoreCase("")||paramRegex.isEmpty()){
            throw new RuntimeException("minLike函数参数为空");
        }
        selectFields.add(" min(/" + paramRegex + "/)");
        return this;
    }

    @Override
    public QueryWrapper percentile(String fieldKey, int num) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("percentile函数参数为空");
        }
        selectFields.add(" percentile(" + fieldKey + "," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper percentileLike(String fieldKeyRegex, int num) {
        if(fieldKeyRegex==null||fieldKeyRegex.equalsIgnoreCase("")||fieldKeyRegex.isEmpty()){
            throw new RuntimeException("percentileLike函数参数为空");
        }
        selectFields.add(" percentile(/" + fieldKeyRegex + "/," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper sample(String fieldKey, int num) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("sample函数参数为空");
        }
        selectFields.add(" sample(" + fieldKey + "," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper sampleLike(String fieldKeyRegex, int num) {
        if(fieldKeyRegex==null||fieldKeyRegex.equalsIgnoreCase("")||fieldKeyRegex.isEmpty()){
            throw new RuntimeException("sampleLike函数参数为空");
        }
        selectFields.add(" sample(/" + fieldKeyRegex + "/," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper top(String fieldKey, int num) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("top函数参数为空");
        }
        selectFields.add(" top(" + fieldKey + "," + num + ")");
        return this;
    }

    @Override
    public QueryWrapper top(String fieldKey, int num, String... tagsKeys) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("top函数参数为空");
        }
        if(tagsKeys==null||tagsKeys.length==0||tagsKeys.equals(null)){
            throw new RuntimeException("top函数参数为空");
        }

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
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("derivative函数参数为空");
        }
        selectFields.add(" derivative(" + param + ")");
        return this;
    }

    @Override
    public QueryWrapper derivative(String param, String time) {
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("derivative函数参数为空");
        }
        if(time==null||time.equalsIgnoreCase("")||time.isEmpty()){
            throw new RuntimeException("derivative函数参数为空");
        }
        selectFields.add(" derivative(" + param + "," + time + ")");
        return this;
    }

    @Override
    public QueryWrapper derivativeLike(String param) {
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("derivativeLike函数参数为空");
        }
        selectFields.add(" derivative(/" + param + "/)");
        return this;
    }

    @Override
    public QueryWrapper derivativeLike(String param, String time) {
        if(param==null||param.equalsIgnoreCase("")||param.isEmpty()){
            throw new RuntimeException("derivativeLike函数参数为空");
        }
        if(time==null||time.equalsIgnoreCase("")||time.isEmpty()){
            throw new RuntimeException("derivativeLike函数参数为空");
        }
        selectFields.add(" derivative(/" + param + "/," + time + ")");
        return this;
    }

    @Override
    public QueryWrapper abs(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("abs函数参数为空");
        }
        selectFields.add(" ABS(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper acos(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("acos函数参数为空");
        }
        selectFields.add(" ACOS(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper cos(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("cos函数参数为空");
        }
        selectFields.add(" COS(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper cumulative_sum(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("cumulative_sum函数参数为空");
        }
        selectFields.add(" CUMULATIVE_SUM(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper cumulative_sumLike(String regex) {
        if(regex==null||regex.equalsIgnoreCase("")||regex.isEmpty()){
            throw new RuntimeException("cumulative_sumLike函数参数为空");
        }
        selectFields.add(" CUMULATIVE_SUM(/" + regex + "/)");
        return this;
    }

    @Override
    public QueryWrapper asin(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("asin函数参数为空");
        }
        selectFields.add(" ASIN(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper sin(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("sin函数参数为空");
        }
        selectFields.add(" SIN(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper sqrt(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("sqrt函数参数为空");
        }
        selectFields.add(" SQRT(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper atan(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("atan函数参数为空");
        }
        selectFields.add(" ATAN(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper tan(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("tan函数参数为空");
        }
        selectFields.add(" TAN(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper atan2(String fieldKey1, String fieldKey2) {
        if(fieldKey1==null||fieldKey1.equalsIgnoreCase("")||fieldKey1.isEmpty()){
            throw new RuntimeException("atan2函数参数为空");
        }
        if(fieldKey2==null||fieldKey2.equalsIgnoreCase("")||fieldKey2.isEmpty()){
            throw new RuntimeException("atan2函数参数为空");
        }
        selectFields.add(" ATAN2(" + fieldKey1 + " , " + fieldKey2 + ")");
        return this;
    }

    @Override
    public QueryWrapper ceil(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("ceil函数参数为空");
        }
        selectFields.add(" CEIL(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper floor(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("floor函数参数为空");
        }
        selectFields.add(" FLOOR(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper ln(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("ln函数参数为空");
        }
        selectFields.add(" LN(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper log(String fieldKey, String b) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("log函数参数为空");
        }
        selectFields.add(" LOG(" + fieldKey + " , " + b + ")");
        return this;
    }

    @Override
    public QueryWrapper log2(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("log2函数参数为空");
        }
        selectFields.add(" LOG2(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper log10(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("log10函数参数为空");
        }
        selectFields.add(" LOG10(" + fieldKey + ")");
        return this;
    }


    @Override
    public QueryWrapper moving_average(String fieldKey, String n) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("moving_average函数参数为空");
        }
        selectFields.add(" MOVING_AVERAGE(" + fieldKey + " , " + n + ")");
        return this;
    }

    @Override
    public QueryWrapper moving_averageLike(String regex, String n) {
        if(regex==null||regex.equalsIgnoreCase("")||regex.isEmpty()){
            throw new RuntimeException("moving_averageLike函数参数为空");
        }
        if(n==null||n.equalsIgnoreCase("")||n.isEmpty()){
            throw new RuntimeException("moving_averageLike函数参数为空");
        }
        selectFields.add(" MOVING_AVERAGE(/" + regex + "/ , " + n + ")");
        return this;
    }

    @Override
    public QueryWrapper non_negative_derivative(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("non_negative_derivative函数参数为空");
        }
        selectFields.add(" NON_NEGATIVE_DERIVATIVE(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper non_negative_derivativeLike(String regex) {
        if(regex==null||regex.equalsIgnoreCase("")||regex.isEmpty()){
            throw new RuntimeException("non_negative_derivativeLike函数参数为空");
        }
        selectFields.add(" NON_NEGATIVE_DERIVATIVE(/" + regex + "/)");
        return this;
    }

    @Override
    public QueryWrapper non_negative_derivative(String fieldKey, String unit) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("non_negative_derivativeLike函数参数为空");
        }
        if(unit==null||unit.equalsIgnoreCase("")||unit.isEmpty()){
            throw new RuntimeException("non_negative_derivative函数参数为空");
        }
        selectFields.add(" NON_NEGATIVE_DERIVATIVE(" + fieldKey + " , " + unit + ")");
        return this;
    }

    @Override
    public QueryWrapper non_negative_derivativeLike(String regex, String unit) {
        if(regex==null||regex.equalsIgnoreCase("")||regex.isEmpty()){
            throw new RuntimeException("non_negative_derivativeLike函数参数为空");
        }
        if(unit==null||unit.equalsIgnoreCase("")||unit.isEmpty()){
            throw new RuntimeException("non_negative_derivative函数参数为空");
        }
        selectFields.add(" NON_NEGATIVE_DERIVATIVE(/" + regex + " , " + unit + "/)");
        return this;
    }

    @Override
    public QueryWrapper non_negative_difference(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("non_negative_derivativeLike函数参数为空");
        }
        selectFields.add(" NON_NEGATIVE_DIFFERENCE(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper non_negative_differenceLike(String regex) {
        if(regex==null||regex.equalsIgnoreCase("")||regex.isEmpty()){
            throw new RuntimeException("non_negative_differenceLike函数参数为空");
        }
        selectFields.add(" NON_NEGATIVE_DIFFERENCE(/" + regex + "/)");
        return this;
    }

    @Override
    public QueryWrapper pow(String fieldKey, String x) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("pow函数参数为空");
        }
        if(x==null||x.equalsIgnoreCase("")||x.isEmpty()){
            throw new RuntimeException("non_negative_derivativeLike函数参数为空");
        }
        selectFields.add(" POW(" + fieldKey + " , " + x + ")");
        return this;
    }

    @Override
    public QueryWrapper round(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("round函数参数为空");
        }
        selectFields.add(" ROUND(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper difference(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("difference函数参数为空");
        }
        selectFields.add(" DIFFERENCE(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper differenceLike(String regex) {
        if(regex==null||regex.equalsIgnoreCase("")||regex.isEmpty()){
            throw new RuntimeException("differenceLike函数参数为空");
        }
        selectFields.add(" DIFFERENCE(/" + regex + "/)");
        return this;
    }

    @Override
    public QueryWrapper elapsed(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("elapsed函数参数为空");
        }
        selectFields.add(" ELAPSED(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper elapsed(String fieldKey, String unit) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("elapsed函数参数为空");
        }
        if(unit==null||unit.equalsIgnoreCase("")||unit.isEmpty()){
            throw new RuntimeException("elapsed函数参数为空");
        }
        selectFields.add(" ELAPSED(" + fieldKey + " , " + unit + ")");
        return this;
    }

    @Override
    public QueryWrapper elapsedLike(String regex) {
        if(regex==null||regex.equalsIgnoreCase("")||regex.isEmpty()){
            throw new RuntimeException("elapsedLike函数参数为空");
        }
        selectFields.add(" ELAPSED(/" + regex + "/)");
        return this;
    }

    @Override
    public QueryWrapper elapsedLike(String regex, String unit) {
        if(regex==null||regex.equalsIgnoreCase("")||regex.isEmpty()){
            throw new RuntimeException("elapsedLike函数参数为空");
        }
        if(unit==null||unit.equalsIgnoreCase("")||unit.isEmpty()){
            throw new RuntimeException("elapsedLike函数参数为空");
        }
        selectFields.add(" ELAPSED(/" + regex + "/ , " + unit + ")");
        return this;
    }

    @Override
    public QueryWrapper exp(String fieldKey) {
        if(fieldKey==null||fieldKey.equalsIgnoreCase("")||fieldKey.isEmpty()){
            throw new RuntimeException("exp函数参数为空");
        }
        selectFields.add(" EXP(" + fieldKey + ")");
        return this;
    }

    @Override
    public QueryWrapper funToFun(String outFunName, String inFunName, String... params) {
        if(outFunName==null||outFunName.equalsIgnoreCase("")||outFunName.isEmpty()){
            throw new RuntimeException("exp函数参数为空");
        }
        if(inFunName==null||inFunName.equalsIgnoreCase("")||inFunName.isEmpty()){
            throw new RuntimeException("exp函数参数为空");
        }
        if(params==null||params.length==0||params.equals(null)){
            throw new RuntimeException("exp函数参数为空");
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" " + outFunName + "(");
        stringBuffer.append(inFunName + "(");
        String s = String.join(",", params);
        stringBuffer.append(s);
        stringBuffer.append(")");
        stringBuffer.append(") ");

        String ss = stringBuffer.toString();
        selectFields.add(ss);
        return this;
    }

//    @Override
//    public QueryWrapper mathematicalOperators(String value, String operator, boolean flag, String funName, String... params) {
//        StringBuffer stringBuffer=new StringBuffer();
//        StringBuffer str=new StringBuffer();
//
//        stringBuffer.append(funName+"(");
//        String s = String.join(",", params);
//        stringBuffer.append(s);
//        stringBuffer.append(")");
//
//        String ss=stringBuffer.toString();
//
//        /**
//         * 如果为真，就value在前，函数在后
//         */
//        if(flag){
//            str.append(value);
//            str.append(" "+operator+" ");
//            str.append(ss);
//        }else {
//            str.append(ss);
//            str.append(" "+operator+" ");
//            str.append(value);
//        }
//
//        selectFields.add(str.toString());
//        return this;
//    }

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
