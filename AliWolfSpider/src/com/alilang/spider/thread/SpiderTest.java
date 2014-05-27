package com.alilang.spider.thread;



import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.alilang.spider.data.webclient.WeiBoUtil;
import com.alilang.spider.dbdao.WbDataDao;
import com.alilang.spider.web.support.ContextloaderListener;

public class SpiderTest {
	
	private static final Logger logger = Logger.getLogger(SpiderTest.class);
	//@Test
	public void ThreadTest(){
		String name = "罗永浩";
		WeiBoUtil weiBoUtil = new WeiBoUtil();
		LinkedList<ThreadG> list = new LinkedList<ThreadG>();
		for (int i=1 ; i < 30;i ++){
			GetWeiBoThread gbt = new GetWeiBoThread(weiBoUtil, name, i);
			ThreadG tg = new ThreadG(gbt);
			list.add(tg);
			ThreadPool.putIntoPool(tg); //放入线程池开始运行
		}
		
		
		
		ThreadG tg = list.poll();
		while (tg != null){
			try {
				tg.join();     //等待该线程执行完再执行主线程
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tg = list.poll();
		}
		System.out.println("主线程继续运行");
	}
	
	
	
	
	
	
	
	
	/**
	 *  通过线程池来进行并发抓取 
	 */
	@Test
	public void batchFetchTest(){
		new ContextloaderListener().init();
		String time = "2014-03-24 10:07";
		//String name = "罗永浩";
		WbDataDao wdd = new WbDataDao();
		LinkedList<ThreadG> list = new LinkedList<ThreadG>();
		LinkedList<String> friendlist = WeiBoUtil.friendsList;
		for(String name : friendlist){
			for (int i=1 ; i < 20;i ++){
				GetWeiboDBThread gbt = new GetWeiboDBThread(wdd, name, i,time);
				ThreadG tg = new ThreadG(gbt);
				list.add(tg);
				ThreadPool.putIntoPool(tg); //放入线程池开始运行
			}
		}
		
		
		
		ThreadG tg = list.poll();
		while (tg != null){
			try {
				tg.join();     //等待该线程执行完再执行主线程
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tg = list.poll();
		}
		logger.debug("主线程继续运行");
	}

}
