package com.alilang.spider.thread;

import com.alilang.spider.data.webclient.WeiBoUtil;
/**
 * 抓取微博信息测试，没有数据库操作
 * @author wuqiang.gwq
 *
 */
public class GetWeiBoThread implements Runnable{
	
	private WeiBoUtil weiBoUtil;
	private String bozhuName;
	private int page;
	
	public GetWeiBoThread(WeiBoUtil weiBoUtil,String bozhuName,int page){
		this.weiBoUtil = weiBoUtil;
		this.bozhuName = bozhuName;
		this.page = page;
	}

	@Override
	public void run() {
		weiBoUtil.getWeiboContent(bozhuName, page);
	}

}
