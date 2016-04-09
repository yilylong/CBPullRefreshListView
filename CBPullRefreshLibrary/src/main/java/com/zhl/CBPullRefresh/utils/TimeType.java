package com.zhl.CBPullRefresh.utils;

public enum TimeType {
	/**
	 * 普通的字符串形式（yyyy-MM-dd hh:mm:ss）
	 */
	DEFAULT,
	
	/**
	 * 普通的字符串中文形式（yyyy年MM月dd日 hh时mm分ss秒）
	 */
	DEFAULT_CHINESS,
	
	/**
	 * 普通时间的短形式（MM-dd HH:mm）
	 */
	DEFAULT_SHORT,
	
	/**
	 * 时间的中文形式（MM月dd日 hh时mm分）
	 */
	DEFAULT_CHINESS_SHORT,
	
	/**
	 * 距离现在已过多久（例如：1分钟之前；1小时之前，1天之前）
	 */
	FROMNOW,
	
	/**
	 * 时间的年月日 （yyyy-MM-dd）
	 */
	DEFAULT_YEAR;
}