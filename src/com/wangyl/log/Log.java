package com.wangyl.log;

import com.wangyl.config.Config;
import com.wangyl.tools.IOapi;
import com.wangyl.tools.Timer;

public class Log {
	private static IOapi ioforlog=new IOapi(1);
	private static boolean islogon=false;
	public static void StartLog() {
		islogon=true;
		ioforlog.startWrite(Config.logsrc, Config.encodingType, 0);
	}
	public static void EndLog() {
		islogon=false;
		ioforlog.endWrite(0);
	}
	public static void LogInf(String s) {
		if(islogon==true) {
			ioforlog.writeOneString(Timer.GetNowTimeToMillisecends()+s+"\n", 0);
			if(Config.isDebugMode) {
				System.out.println(Timer.GetNowTimeToMillisecends()+s+"\n");
			}
		}
		else if(Config.isDebugMode){
			System.out.println(Timer.GetNowTimeToMillisecends()+s+"\n");
		}
	}
}
