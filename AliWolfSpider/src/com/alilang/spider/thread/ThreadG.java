package com.alilang.spider.thread;


public class ThreadG extends Thread{

	public ThreadG() {
		
		super();
		// TODO Auto-generated constructor stub
	}

	public ThreadG(Runnable target, String name) {
		super(target, name);
		// TODO Auto-generated constructor stub
	}

	public ThreadG(Runnable target) {
		super(target);
		// TODO Auto-generated constructor stub
	}

	public ThreadG(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public ThreadG(ThreadGroup group, Runnable target, String name,
			long stackSize) {
		super(group, target, name, stackSize);
		// TODO Auto-generated constructor stub
	}

	public ThreadG(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		// TODO Auto-generated constructor stub
	}

	public ThreadG(ThreadGroup group, Runnable target) {
		super(group, target);
		// TODO Auto-generated constructor stub
	}

	public ThreadG(ThreadGroup group, String name) {
		super(group, name);
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		//System.out.println(this.getId()+"线程结束！！！");
		try {
			currentThread().sleep(1000*2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ThreadPool.removeFromPool(this.getId());
	}

	@Override
	public void interrupt() {
		//System.out.println(this.getId()+"中断了！！！");
		super.interrupt();
	}

	

}
