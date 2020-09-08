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
    QueryWrapper eqTag(Object key, Object value);

    /**
     * tagvalue不等于某个值
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper neTag(Object key, Object value);

    /**
     * field等于某个值
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper eqField(Object key, Object value);

    /**
     * 不等于
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper neField(Object key, Object value);

    /**
     * 小于
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper ltField(Object key, Object value);

    /**
     * 大于
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper gtField(Object key, Object value);

    /**
     * 大于等于
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper geField(Object key, Object value);

    /**
     * 小于等于
     *
     * @param key
     * @param value
     * @return
     */
    QueryWrapper leField(Object key, Object value);


    /**
     * 条件是 过去的 value 秒/分钟/小时  到现在的时间间隔之间
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
     * @param time 每一组的时间间隔
     * @return
     */
    QueryWrapper groupByTime(String time);

    /**
     *
     * @param time
     * @param offsetTime 向前或者向后移动 预设的时间界限
     * @return
     */
    QueryWrapper groupByTime(String time,String offsetTime);

    /**
     * fill函数
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
    QueryWrapper whereLike(Object key, String regex);

    QueryWrapper whereNotLike(Object key, String regex);

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
    QueryWrapper cumulative_Sum(String param);

    QueryWrapper cumulative_SumLike(String regex);


    /**
     * derivative函数
     * @param param
     * @return
     */
    QueryWrapper derivative(String param);
    QueryWrapper derivative(String param,String time);
    QueryWrapper derivativeLike(String param);
    QueryWrapper derivativeLike(String param,String time);

    /**
     * 获取拼接成的sql语句
     *
     * @return
     */
    String getSqlSelect();

}