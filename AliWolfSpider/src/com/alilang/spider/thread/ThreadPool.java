package com.alilang.spider.thread;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;


public class ThreadPool {

	private static final Logger logger = Logger.getLogger(ThreadPool.class);
	private static final Map<Long,ThreadG> activThreadMap = new HashMap<Long,ThreadG>();
	private static final LinkedBlockingQueue<ThreadG> waitThreadQueue = new LinkedBlockingQueue<ThreadG>();
	private static int activeSize = 1;//允许同时运行的线程数量
	
	public static synchronized void putIntoPool(ThreadG threadg){
		try {
			waitThreadQueue.put(threadg);
			changeThread();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
    static synchronized void removeFromPool(long threadId){
    	activThreadMap.remove(threadId);
    	changeThread();
    	
    }
    
    private static void changeThread(){
    	if(activThreadMap.size() < activeSize){
			ThreadG tg = waitThreadQueue.poll();
			if(tg != null){
				logger.debug(tg.getId()+"进入活动队列，启动运行！！！！");
				activThreadMap.put(tg.getId(), tg);
				tg.start();
			}
		}
    	logger.debug("活动队列中有"+activThreadMap.size()+"个线程，等待队列中有"+waitThreadQueue.size()+"个线程");
    }
}
