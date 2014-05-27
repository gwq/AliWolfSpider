package com.alilang.spider.thread;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;


public class ThreadPool {

	private static final Logger logger = Logger.getLogger(ThreadPool.class);
	private static final Map<Long,ThreadG> activThreadMap = new HashMap<Long,ThreadG>();
	private static final LinkedBlockingQueue<ThreadG> waitThreadQueue = new LinkedBlockingQueue<ThreadG>();
	private static int activeSize = 1;//����ͬʱ���е��߳�����
	
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
				logger.debug(tg.getId()+"�������У��������У�������");
				activThreadMap.put(tg.getId(), tg);
				tg.start();
			}
		}
    	logger.debug("���������"+activThreadMap.size()+"���̣߳��ȴ���������"+waitThreadQueue.size()+"���߳�");
    }
}
