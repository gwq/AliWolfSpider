package com.alilang.spider.web.support;

import java.util.LinkedList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.alilang.spider.data.webclient.AccountProperty;
import com.alilang.spider.data.webclient.WeiBoUtil;
import com.alilang.spider.db.DBPool;
import com.alilang.spider.db.DBProperty;
import com.alilang.spider.dbdao.WbDataDao;
import com.alilang.spider.thread.GetWeiboDBThread;
import com.alilang.spider.thread.TaskThread;
import com.alilang.spider.thread.ThreadG;
import com.alilang.spider.thread.ThreadPool;

public class ContextloaderListener implements ServletContextListener{
	
	private static final Logger logger = Logger.getLogger(ContextloaderListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("Server Start!!!");
		init();
		if (AccountProperty.getInit() ==1 ){
			batchFetchInit();
		}
		new Thread(new TaskThread()).start();
	}
	
	/**
	 * 初始话数据库和其他配置
	 */
	public void init(){
		DBProperty.init();
		AccountProperty.init();
		DBPool.initDBPool();
		WeiBoUtil.friendsList = new WeiBoUtil().getFriendsByAPI();
	}
	
	public void batchFetchInit(){
		//new ContextloaderListener().init();
		//String time = "2014-05-26 00:00";
		String time = null;
		WbDataDao wdd = new WbDataDao();
		LinkedList<ThreadG> list = new LinkedList<ThreadG>();
		LinkedList<String> friendlist = WeiBoUtil.friendsList;
		logger.debug("User Number : "+friendlist.size());
		//for(String name : friendlist){
		for (int j = 0 ;j < 165 ;j ++){  //当前频率下查询超过165次会被封号
			for (int i=1 ; i < 2;i ++){
				GetWeiboDBThread gbt = new GetWeiboDBThread(wdd, friendlist.get(j), i,time);
				ThreadG tg = new ThreadG(gbt);
				list.add(tg);
				ThreadPool.putIntoPool(tg); //放入线程池开始运行
			}
		//}
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
		logger.debug("the Main Thread Continue....");
	}

}
