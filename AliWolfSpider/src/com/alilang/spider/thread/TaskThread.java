package com.alilang.spider.thread;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alilang.spider.dbdao.WbDataDao;

public class TaskThread implements Runnable{
	private SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	@Override
	public void run() {
		WbDataDao wdd = new WbDataDao();
		while (true){
			long currentTime = System.currentTimeMillis();
			String dateStr = msdf.format(new Date(currentTime));
			try {
				Thread.currentThread().sleep(1000*60*6);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			wdd.insertWeibo("",1,dateStr,true);
			
		}
		
	}
	

}
