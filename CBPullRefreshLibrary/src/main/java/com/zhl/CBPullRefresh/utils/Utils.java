package com.zhl.cbpullrefresh.utils;

import android.content.Context;

import com.zhl.cbpullrefresh.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zhaohl on 2016-3-24.
 */
public class Utils {
    public static String getTimeDifferent(Context context,long time) {
        String updateTime = "";
        Date currentTime = new Date(System.currentTimeMillis());
        Date inputTime = new Date(time);
        long interval = currentTime.getTime() - inputTime.getTime();
        long days = interval / 86400000;
        long hours = (interval - days * 86400000) / 3600000;
        long minutes = (interval - days * 86400000 - hours * 3600000) / 60000;
        if (days >= 1) {
            updateTime = getPublishTime(context,TimeType.DEFAULT_YEAR, inputTime.getTime());
        } else if (hours >= 1) {
            updateTime = hours +context.getString(R.string.util_hours_ago) ;
        } else {
            if (minutes <= 10) {
                updateTime = context.getString(R.string.util_a_moment_ago);
            } else if (minutes > 10 && minutes <= 20) {
                updateTime = context.getString(R.string.util_10_moment_ago);
            } else if (minutes > 20 && minutes <= 30) {
                updateTime = context.getString(R.string.util_20_moment_ago);
            } else if (minutes > 30 && minutes < 60) {
                updateTime = context.getString(R.string.util_30_moment_ago);
            }
        }
        return updateTime;
    }

    public static String getPublishTime(Context context ,TimeType type, long time) {
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
                dateFormat = new SimpleDateFormat(context.getString(R.string.util_format_yyyymmdd_hhmmss), Locale.CHINA);
                timeStr = dateFormat.format(date);
                break;
            case DEFAULT_SHORT:
                dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
                timeStr = dateFormat.format(date);
                break;
            case DEFAULT_CHINESS_SHORT:
                dateFormat = new SimpleDateFormat(context.getString(R.string.util_format_mmdd_hhmm), Locale.CHINA);
                timeStr = dateFormat.format(date);
                break;
            case FROMNOW:
                timeStr = getTimeFromNow(context,time);
                break;
            case DEFAULT_YEAR:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                timeStr = dateFormat.format(date);
                break;
        }
        return timeStr;
    }

    public static String getTimeFromNow(Context context,long time) {
        String updateTime = "";
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - time;
        long minutes = interval / 60000;
        if (minutes < 60) {
            minutes = minutes == 0 ? 1 : minutes;
            updateTime = minutes + context.getString(R.string.util_miniutes_ago);
        } else {
            minutes = minutes / 60;
            minutes = minutes == 0 ? 1 : minutes;
            if (minutes < 24) {
                updateTime = minutes + context.getString(R.string.util_hours_ago);
            } else {
                minutes = minutes / 24;
                minutes = minutes == 0 ? 1 : minutes;
                updateTime = minutes + context.getString(R.string.util_day_ago);
            }
        }
        return updateTime;
    }
}
