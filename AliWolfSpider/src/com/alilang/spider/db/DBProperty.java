package com.alilang.spider.db;



import java.io.InputStream;
import java.util.Properties;

public class DBProperty {
	
	private static String className;
	private static String name;
	private static String passwd;
	private static String url;
	
	public static void init (){
		Properties p = createProperty();
		className = p.getProperty("className");
		name = p.getProperty("name");
		passwd = p.getProperty("password");
		url = p.getProperty("url");
		
	}
	
	public static String getClassName() {
		return className;
	}

	public static String getName() {
		return name;
	}

	public static String getPassword() {
		return passwd;
	}

	public static String getUrl() {
		return url;
	}

	private static Properties createProperty() {
		InputStream in;
		Properties p = new Properties();
		try {
			in = Class.forName("com.alilang.spider.db.DBProperty").getResourceAsStream("/DB.properties");
			p.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}

}
