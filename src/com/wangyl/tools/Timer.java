package com.wangyl.tools;

import java.util.Calendar;

public class Timer {
	/**
	 * @author WangYunli
	 * @return 当前时间，精确到毫秒
	 */
	public static String GetNowTimeToMillisecends() {
		Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
		int year = c.get(Calendar.YEAR); 
		int month = c.get(Calendar.MONTH); 
		int date = c.get(Calendar.DATE); 
		int hour = c.get(Calendar.HOUR_OF_DAY); 
		int minute = c.get(Calendar.MINUTE); 
		int second = c.get(Calendar.SECOND);
		int millisecond=c.get(Calendar.MILLISECOND);
		return year + "/" + month + "/" + date + " " +hour + ":" +minute + ":" + second+":"+millisecond+" "; 
	}
	public static void main(String[] args) {
    	System.out.println(GetNowTimeToMillisecends());
    }
}
