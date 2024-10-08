package com.wgx.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.wgx.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * 工具类
 *
 * @author wgx
 * @date 2024/1/30 19:19
 */
public class DataUtil {
    private static final Logger logger = LogManager.getLogger(DataUtil.class);

    public static UserInfo getProperty() {
        UserInfo info = new UserInfo();
        //通过反射获取property中的配置
        Properties properties = new Properties();
        try {
            properties.load(DataUtil.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            logger.error("丢失配置文件application");
            logger.error(e.getMessage(), e);
        }
        //默认到d盘下test文件
        info.setSavePath(properties.getProperty("savePath", "d:/test"));
        info.setScreenName(properties.getProperty("screenName"));
        info.setCookie(properties.getProperty("cookie"));
        info.setTimeRange(properties.getProperty("timeRange", "1990-01-01;2030-01-01"));
        info.setProxy(properties.getProperty("proxy"));
        info.setUserMediaTop(properties.getProperty("UserMediaTop"));
        info.setUserMediaBottom(properties.getProperty("UserMediaBottom"));
        info.setUserByScreenName(properties.getProperty("UserByScreenName"));
        info.setIsNeedEXecl(StrUtil.isNotBlank(properties.getProperty("isNeedEXecl")) && Boolean.parseBoolean(properties.getProperty("isNeedEXecl")));
        info.setIsNeedPhoto(StrUtil.isBlank(properties.getProperty("isNeedPhoto")) || Boolean.parseBoolean(properties.getProperty("isNeedPhoto")));
        //properties.getProperty("isNeedVideo","true")这个是没有isNeedVideo返回true，并不是为空返回true
        info.setIsNeedVideo(StrUtil.isBlank(properties.getProperty("isNeedVideo")) || Boolean.parseBoolean(properties.getProperty("isNeedVideo")));
        info.setExeclHead(properties.getProperty("execlHead"));
        return info;
    }


    /**
     * 根据传进来的日期判断是否在配置文件的符合日期内(有配置文件以配置文件为准,没有配置文件则增量下载)
     *
     * @param param         传入日期
     * @param timeRange     日期区间
     * @param maxModifyDate 文件夹中的最大修改时间
     */
    public static boolean compareDate(Date param, String timeRange, Date maxModifyDate) {
        if (StrUtil.isNotBlank(timeRange)) {
            String[] split = timeRange.split(";");
            Date start = DateUtil.parse(split[0]);
            Date end = DateUtil.parse(split[1]);
            //param>start && end>param
            return DateUtil.compare(param, start) > 0 && DateUtil.compare(end, param) > 0;
        } else {
            //param>maxModifyDate
            return DateUtil.compare(param, maxModifyDate) > 0;
        }
    }

    /**
     * 判断推文日期是否小于设置的最小日期(有配置文件以配置文件为准,没有配置文件则增量下载)
     *
     * @param param         传入日期
     * @param timeRange     日期区间
     * @param maxModifyDate 文件夹中的最大修改时间
     */
    public static boolean checkMinDate(Date param, String timeRange, Date maxModifyDate) {
        if (StrUtil.isNotBlank(timeRange)) {
            String[] split = timeRange.split(";");
            Date start = DateUtil.parse(split[0]);
            //date1 < date2，返回数小于0
            return DateUtil.compare(param, start) < 0;
        } else {
            return DateUtil.compare(param, maxModifyDate) < 0;
        }
    }

    /**
     * 处理下文本值
     */
    public static String dealString(String title) {
        // 去除最后一个换行之后的内容,去除掉http后面的所有字符,去除掉文件名不支持的特殊字符
        title = title.replaceAll("(\\r?\\n)$", "").replaceAll("https.*", "").replaceAll("[\\\\/:*?<>|]", "");
        // 去除掉@后面的所有字符
        if (title.indexOf("@") > 0) {
            title = title.replaceAll("@.*", "");
        }
        //去除掉所有空格及换行
        return title.replaceAll(" +|\\n", "");
    }

}
