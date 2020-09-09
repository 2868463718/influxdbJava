package zy.blue7.influxdbjava.wapper;


public interface QueryWrapper {
    /**
     * 设置要查询的字段
     *
     * @param sqlSelect
     * @return
     */
    QueryWrapper select(String... sqlSelect);

    /**
     * 设置要插入的表
     *
     * @param measurement
     * @return
     */
    QueryWrapper into(String measurement);

    /**
     * 设置要查询的表
     *
     * @param sqlFrom
     * @return
     */
    QueryWrapper from(String... sqlFrom);

    /**
     * tagvalue等于某个值
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper eqTag(String key, String value);

    /**
     * tagvalue不等于某个值
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper neTag(String key, String value);

    /**
     * field等于某个值
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper eqField(String key, Object value);

    /**
     * 不等于
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper neField(String key, Object value);

    /**
     * 小于
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper ltField(String key, Object value);

    /**
     * 大于
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper gtField(String key, Object value);

    /**
     * 大于等于
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper geField(String key, Object value);

    /**
     * 小于等于
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper leField(String key, Object value);


    /**
     * 条件是 过去的 value 秒/分钟/小时  到现在的时间间隔之间
     *
     * @param value
     * @return
     */
    QueryWrapper timeNow(String value);


    /**
     * 根据什么tagkey分组
     *
     * @param tagsKey
     * @return
     */
    QueryWrapper groupBy(String... tagsKey);


    /**
     * 通过时间来 分组，
     *
     * @param time 每一组的时间间隔
     * @return
     */
    QueryWrapper groupByTime(String time);

    /**
     * @param time
     * @param offsetTime 向前或者向后移动 预设的时间界限
     * @return
     */
    QueryWrapper groupByTime(String time, String offsetTime);

    /**
     * fill函数
     *
     * @param fillStr
     * @return
     */
    QueryWrapper fill(String fillStr);


    /**
     * 升序降序
     *
     * @param orderBy
     * @return
     */
    QueryWrapper orderByTime(String orderBy);

    /**
     * 返回没个series中的前 size 个记录
     *
     * @param size
     * @return
     */
    QueryWrapper limit(int size);

    /**
     * 返回前 size 个series
     *
     * @param size
     * @return
     */
    QueryWrapper sLimit(int size);

    /**
     * 返回前 sLSize 个 series，并且每个series只返回前 lSize 个记录
     *
     * @param lSize
     * @param sLSize
     * @return
     */
    QueryWrapper limitAndSLimit(int lSize, int sLSize);

    /**
     * 分页查询（相对于所有记录）
     *
     * @param pageNum
     * @return
     */
    QueryWrapper offset(int pageNum);

    /**
     * 分页查询，（相对于所有series）
     *
     * @param pageNum
     * @return
     */
    QueryWrapper sOffset(int pageNum);


    /**
     * where子句连接两个条件 之间 为 or
     *
     * @return
     */
    QueryWrapper or();

    /**
     * where子句连接两个条件 之间 为 and
     *
     * @return
     */
    QueryWrapper and();


    /**
     * 正则表达式 用在 要查询的字段上
     *
     * @param selectRegex
     * @return
     */
    QueryWrapper selectLike(String... selectRegex);

    /**
     * 正则表达式用在 从那些表中查询上
     *
     * @param fromRegex
     * @return
     */
    QueryWrapper fromLike(String... fromRegex);

    /**
     * 正则表达式用在 where 子句上
     *
     * @param key
     * @param regex
     * @return
     */
    QueryWrapper whereLike(String key, String regex);

    QueryWrapper whereNotLike(String key, String regex);

    /**
     * groupby 使用正则表达式
     *
     * @param tagsRegex
     * @return
     */
    QueryWrapper groupByLike(String... tagsRegex);

    /**
     * count 函数
     *
     * @param param
     * @return
     */
    QueryWrapper count(String param);

    /**
     * count函数 参数使用正则表达式
     *
     * @param paramRegex
     * @return
     */
    QueryWrapper countLike(String paramRegex);


    /**
     * distinct函数
     *
     * @param param
     * @return
     */
    QueryWrapper distinct(String param);

    /**
     * 统计 去重后 有多少个 数据
     *
     * @param param
     * @return
     */
    QueryWrapper countDistinct(String param);

    /**
     * distinct函数参数使用 正则表达式，不支持，只支持一个字段去重
     * @param paramRegex
     * @return
     */
//    QueryWrapper distinctLike(String paramRegex);


    /**
     * integral函数
     *
     * @param integralParam
     * @return
     */
    QueryWrapper integral(String integralParam);

    QueryWrapper integral(String integralParam, String time);

    /**
     * integral 函数 参数使用正则表达式
     *
     * @param integralParamRegex
     * @return
     */
    QueryWrapper integralLike(String integralParamRegex);

    QueryWrapper integralLike(String integralParamRegex, String time);


    /**
     * mean函数，求平均值
     *
     * @param meanParam
     * @return
     */
    QueryWrapper mean(String meanParam);

    QueryWrapper meanLike(String meanRegex);

    /**
     * median函数  求中位数
     *
     * @param medianParam
     * @return
     */
    QueryWrapper median(String medianParam);

    QueryWrapper medianLike(String medianRegex);


    /**
     * mode 函数，返回 指定字段 出现频率最高的字段值
     *
     * @param modeParam
     * @return
     */
    QueryWrapper mode(String modeParam);

    QueryWrapper modeLike(String modeRegex);

    /**
     * spread函数，求最大值与最小值差值
     *
     * @param spreadParam
     * @return
     */
    QueryWrapper spread(String spreadParam);

    QueryWrapper spreadLike(String spreadParamRegex);

    /**
     * stddev函数 求标准差
     *
     * @param stdDevParam
     * @return
     */
    QueryWrapper stdDev(String stdDevParam);

    QueryWrapper stdDevLike(String stdDevRegex);

    /**
     * sum函数
     *
     * @param sumParam
     * @return
     */
    QueryWrapper sum(String sumParam);

    QueryWrapper sumLike(String sumRegex);


    /**
     * bottom 函数，找出最小的几个值
     *
     * @param fieldKey
     * @param num
     * @return
     */
    QueryWrapper bottom(String fieldKey, int num);

    QueryWrapper bottom(String fieldKey, int num, String... tagsKey);


    /**
     * first函数，返回时间戳最早的字段值
     *
     * @param param
     * @return
     */
    QueryWrapper first(String param);

    QueryWrapper firstLike(String regex);


    /**
     * last函数，返回时间戳最近的字段的值
     *
     * @param param
     * @return
     */
    QueryWrapper last(String param);

    QueryWrapper lastLike(String regex);

    /**
     * max函数，返回最大值
     *
     * @param param
     * @return
     */
    QueryWrapper max(String param);

    /**
     * 当max函数里面参数是 * 或者 正则表达式式，该函数后面不能继续跟字段，即 max（*），name 这个是不成立的，不允许的
     *
     * @param paramRegex
     * @return
     */
    QueryWrapper maxLike(String paramRegex);


    /**
     * min函数，返回最小值
     *
     * @param param
     * @return
     */
    QueryWrapper min(String param);

    /**
     * 当min函数里面参数是 * 或者 正则表达式式，该函数后面不能继续跟字段，即 min（*），name 这个是不成立的，不允许的
     *
     * @param paramRegex
     * @return
     */
    QueryWrapper minLike(String paramRegex);


    /**
     * percentile函数，
     *
     * @param fieldKey
     * @param num
     * @return
     */
    QueryWrapper percentile(String fieldKey, int num);

    /**
     * 当使用正则表达式时，不支持该函数后面存在 其他字段
     *
     * @param fieldKeyRegex
     * @param num
     * @return
     */
    QueryWrapper percentileLike(String fieldKeyRegex, int num);


    /**
     * sample函数
     *
     * @param fieldKey
     * @param num
     * @return
     */
    QueryWrapper sample(String fieldKey, int num);

    /**
     * 参数为正则表达式的函数 的前后不允许有 其他字段
     *
     * @param fieldKeyRegex
     * @param num
     * @return
     */
    QueryWrapper sampleLike(String fieldKeyRegex, int num);


    /**
     * top函数
     *
     * @param fieldKey
     * @param num
     * @return
     */
    QueryWrapper top(String fieldKey, int num);

    QueryWrapper top(String fieldKey, int num, String... tagsKeys);


    /**
     * cumulative_sum函数
     *
     * @param param
     * @return
     */
//    QueryWrapper cumulative_Sum(String param);
//
//    QueryWrapper cumulative_SumLike(String regex);


    /**
     * derivative函数
     * InfluxDB计算后续字段值之间的差，并将这些结果转换为每位的变化率unit。
     * 所述unit参数是一个整数，其后一个持续时间文字，它是可选的。如果查询未指定，unit则单位默认为一秒（1s）。
     *
     * @param param
     * @return
     */
    QueryWrapper derivative(String param);

    QueryWrapper derivative(String param, String time);

    QueryWrapper derivativeLike(String param);

    QueryWrapper derivativeLike(String param, String time);


    /**
     * 返回绝对值
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper abs(String fieldKey);
//    QueryWrapper ABSLike(String regex);

    /**
     * 返回反余炫
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper acos(String fieldKey);


    /**
     * 返回余炫
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper cos(String fieldKey);

    /**
     * 计算累计和
     * 计算与字段键关联的字段值的累积和
     *cumulative_sum
     * @param fieldKey
     * @return
     */
    QueryWrapper cumulative_sum(String fieldKey);

    /**
     * 计算累计和
     * 计算与字段键关联的字段值的累积和
     *
     * @param regex
     * @return
     */
    QueryWrapper cumulative_sumLike(String regex);

    /**
     * 返回 反正弦 值
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper asin(String fieldKey);
    /**
     * 返回 正弦 值
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper sin(String fieldKey);


    QueryWrapper sqrt(String fieldKey);


    /**
     * 返回反正切
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper atan(String fieldKey);

    /**
     * 返回正切
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper tan(String fieldKey);

    /**
     * 返回  fieldKey1/fieldKey2 的反正切，如果fieldKey1 是 *  则匹配多个值  /fieldKey2
     *
     * @param fieldKey1 ---》fieldKey  *   num
     * @param fieldKey2 -----》fieldKey num
     * @return
     */
    QueryWrapper atan2(String fieldKey1, String fieldKey2);


    /**
     * 返回舍入到最接近整数的后续值
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper ceil(String fieldKey);

    /**
     * 返回舍入到最接近整数的后续值。
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper floor(String fieldKey);


    /**
     * 返回字段值的自然对数
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper ln(String fieldKey);

    /**
     * 返回以base为底的字段值的对数b
     *
     * @param fieldKey
     * @param b
     * @return
     */
    QueryWrapper log(String fieldKey, String b);

    /**
     * 返回以 2 为底的字段值的对数
     *
     * @param fieldKey
     * @param
     * @return
     */
    QueryWrapper log2(String fieldKey);

    /**
     * 返回以 10 为底的字段值的对数
     *
     * @param fieldKey
     * @param
     * @return
     */
    QueryWrapper log10(String fieldKey);




    /**
     * 返回窗口的后续字段值的滚动平均值
     *
     * @param fieldKey
     * @param n
     * @return
     */
    QueryWrapper moving_average(String fieldKey, String n);

    QueryWrapper moving_averageLike(String regex, String n);


    QueryWrapper non_negative_derivative(String fieldKey);

    QueryWrapper non_negative_derivativeLike(String regex);

    QueryWrapper non_negative_derivative(String fieldKey, String unit);

    QueryWrapper non_negative_derivativeLike(String regex, String unit);


    QueryWrapper non_negative_difference(String fieldKey);

    QueryWrapper non_negative_differenceLike(String regex);


    QueryWrapper pow(String fieldKey, String x);


    QueryWrapper round(String fieldKey);


    /**
     * 返回后续字段值之间相减的结果。
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper difference(String fieldKey);

    QueryWrapper differenceLike(String regex);

    /**
     * InfluxDB计算后续时间戳之间的差异。
     * 该unit选项是一个整数，后跟持续时间文字，
     * 它确定返回差值的单位。如果查询未指定unit选项，则查询返回时间戳之间的差异（以纳秒为单位）。
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper elapsed(String fieldKey);

    QueryWrapper elapsed(String fieldKey, String unit);

    QueryWrapper elapsedLike(String regex);

    QueryWrapper elapsedLike(String regex, String unit);

    /**
     * 返回字段值的指数。
     *
     * @param fieldKey
     * @return
     */
    QueryWrapper exp(String fieldKey);


    /**
     * 函数嵌套,不支持正则表达式
     * @param outFunName  最外面的函数
     * @param inFunName 里面的函数
     * @param params    里面函数的参数
     * @return
     */
    QueryWrapper funToFun(String outFunName,String inFunName,String... params);


    /**
     *
     * 不支持在函数中使用操作符
     *
     *
     * 对函数进行数学运算符操作，如果flag 为 true ，则value 在函数前面，否则在后面，操作符为operator
     * @param value  要操作的数值
     * @param operator 操作符
     * @param flag
     * @param funName  函数名字
     * @param params   函数参数
     * @return
     */
//    QueryWrapper mathematicalOperators(String value ,String operator,boolean flag,String funName,String... params);


    /**
     * 获取拼接成的sql语句
     *
     * @return
     */
    String getSqlSelect();

}
