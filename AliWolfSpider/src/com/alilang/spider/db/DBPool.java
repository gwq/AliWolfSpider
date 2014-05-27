package com.alilang.spider.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;


public class DBPool {
	
	private static final Logger logger = Logger.getLogger(DBPool.class);
	private static final LinkedBlockingQueue<Connection> connQueue = new LinkedBlockingQueue<Connection>();
	private static int connPoolSize = 7;//数据库连接池中的数量
	
	private static Connection createConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	Connection conn = null;
	//加载数据库驱动类
	Class.forName(DBProperty.getClassName()).newInstance();
	//数据库连接URL
	String url = DBProperty.getUrl();
	//数据库用户名
	String user = DBProperty.getName();
	//数据库密码
	String password = DBProperty.getPassword();
	//根据数据库参数取得一个数据库连接
	conn = DriverManager.getConnection(url, user, password);
	return conn;
	}
	
	/**
	 * 系统初始化时，构建数据库连接池
	 */
	public static void initDBPool(){
		int i = 0;
		while (i < connPoolSize){
			try {
				connQueue.add(createConnection());
			} 
			catch (SQLException e) {
				logger.error("init -- DataBase link Faild!!!");
				logger.error("please check DB property!!!");
				e.printStackTrace();
				System.exit(0);
			}
			catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				logger.error("init -- DataBase link Faild!!!");
				e.printStackTrace();
				System.exit(0);
			} 
			i ++;
		}
		logger.info("DataBase Connection Pool Init！！！");
	}
	
	/**
	 * 从数据库连接池中获取连接，如果连接池中没有就新创建一个连接
	 * @return
	 */
	public synchronized static Connection getConnection(){
		Connection conn = connQueue.poll();
		if (conn != null){
			logger.debug("Current Pool Conn Number : "+connQueue.size());
			return conn;
		}
		try {
			conn = createConnection();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			logger.error("create -- DataBase link Faild!!!");
			e.printStackTrace();
		}
		logger.debug("Current Pool Conn Number : "+connQueue.size());
		return conn;
	}
	
	
	/**
	 * 回收数据库连接，当连接池中的连接数大于设定值，多余的连接将被关闭
	 * @param conn
	 */
	public synchronized static void recycelConn(Connection conn){
		if (connQueue.size() <= connPoolSize){
			connQueue.add(conn);
		}
		else{
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally{
					conn = null;
				}
		}
		logger.debug("Current Pool Conn Number : "+connQueue.size());
	}

    /**
     * 关闭PreparedStatement
     * @param ps
     */
	public static void closePS(PreparedStatement ps) {
		if (ps != null){
			try {
				ps.close();
				ps = null;
			} catch (SQLException e) {
				ps = null;
				e.printStackTrace();
			}
		}
		
	}
}
