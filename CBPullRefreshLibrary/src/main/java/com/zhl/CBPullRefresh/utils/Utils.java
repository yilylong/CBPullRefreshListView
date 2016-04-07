package com.zhl.CBPullRefresh.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 描述：
 * Created by zhaohl on 2016-3-24.
 */
public class Utils {
    public static String getTimeDifferent(long time) {
        String updateTime = "";
        Date currentTime = new Date(System.currentTimeMillis());
        Date inputTime = new Date(time);
        long interval = currentTime.getTime() - inputTime.getTime();
        long days = interval / 86400000;
        long hours = (interval - days * 86400000) / 3600000;
        long minutes = (interval - days * 86400000 - hours * 3600000) / 60000;
        if (days >= 1) {
            updateTime = getPublishTime(TimeType.DEFAULT_YEAR, inputTime.getTime());
        } else if (hours >= 1) {
            updateTime = hours + "小时前";
        } else {
            if (minutes <= 10) {
                updateTime = "刚刚";
            } else if (minutes > 10 && minutes <= 20) {
                updateTime = "10分钟前";
            } else if (minutes > 20 && minutes <= 30) {
                updateTime = "20分钟前";
            } else if (minutes > 30 && minutes < 60) {
                updateTime = "30分钟前";
            }
        }
        return updateTime;
    }

    public static String getPublishTime(TimeType type, long time) {
        if (time == 0)
            return "";
        Date date = new Date(time);
        SimpleDateFormat dateFormat;
        String timeStr = null;
        switch (type) {
            case DEFAULT:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                timeStr = dateFormat.format(date);
                break;
            case DEFAULT_CHINESS:
                dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒", Locale.CHINA);
                timeStr = dateFormat.format(date);
                break;
            case DEFAULT_SHORT:
                dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
                timeStr = dateFormat.format(date);
                break;
            case DEFAULT_CHINESS_SHORT:
                dateFormat = new SimpleDateFormat("MM月dd日 HH时mm分", Locale.CHINA);
                timeStr = dateFormat.format(date);
                break;
            case FROMNOW:
                timeStr = getTimeFromNow(time);
                break;
            case DEFAULT_YEAR:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                timeStr = dateFormat.format(date);
                break;
        }
        return timeStr;
    }

    public static String getTimeFromNow(long time) {
        String updateTime = "";
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - time;
        long minutes = interval / 60000;
        if (minutes < 60) {
            minutes = minutes == 0 ? 1 : minutes;
            updateTime = minutes + "分钟前";
        } else {
            minutes = minutes / 60;
            minutes = minutes == 0 ? 1 : minutes;
            if (minutes < 24) {
                updateTime = minutes + "小时前";
            } else {
                minutes = minutes / 24;
                minutes = minutes == 0 ? 1 : minutes;
                updateTime = minutes + "日前";
            }
        }
        return updateTime;
    }
}
