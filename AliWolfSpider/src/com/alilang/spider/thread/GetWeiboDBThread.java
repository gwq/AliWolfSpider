package com.alilang.spider.thread;

import com.alilang.spider.dbdao.WbDataDao;

/**
 * ץȡ΢����Ϣ����������ݿ�
 * @author wuqiang.gwq
 *
 */
public class GetWeiboDBThread implements Runnable{
	
	private WbDataDao wdd;
	private String username;
	private int page;
	private String dateStr;

	public GetWeiboDBThread(WbDataDao wdd,String username,int page,String dateStr){
		this.wdd = wdd;
		this.username = username;
		this.page = page;
		this.dateStr = dateStr;
	}

	@Override
	public void run() {
		wdd.insertWeibo(username, page, dateStr);
		
	}
}
