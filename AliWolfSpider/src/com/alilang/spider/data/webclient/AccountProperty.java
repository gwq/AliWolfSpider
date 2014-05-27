package com.alilang.spider.data.webclient;

import java.io.InputStream;
import java.util.Properties;

public class AccountProperty {
	
	private static String token;
	private static String accountName;
	private static String accountPasswd;
	private static String screenName;
	private static int init;
	
	public static void init(){
		Properties p = createProperty();
		accountName = p.getProperty("weibo_username");
		accountPasswd = p.getProperty("weibo_passwd");
		screenName = p.getProperty("screenName");
		token = p.getProperty("token");
		init = Integer.valueOf(p.getProperty("init"));
	}
	
	public static int getInit(){
		return init;
	}
	
	public static String getToken(){
		return token;
	}
	
	public static String getScreenName(){
		return screenName;
	}
	
	public static String getAccountName() {
		return accountName;
	}

	public static String getAccountPasswd() {
		return accountPasswd;
	}
	
	
	private static Properties createProperty() {
		InputStream in;
		Properties p = new Properties();
		try {
			//in = this.getClass().getResourceAsStream("/Account.properties");
			in = new Object() {
				public InputStream getResourceAsStream(){
					return this.getClass().getResourceAsStream("/Account.properties");
				}
			}.getResourceAsStream();
			p.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}

}
