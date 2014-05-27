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
	private static int connPoolSize = 7;//���ݿ����ӳ��е�����
	
	private static Connection createConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	Connection conn = null;
	//�������ݿ�������
	Class.forName(DBProperty.getClassName()).newInstance();
	//���ݿ�����URL
	String url = DBProperty.getUrl();
	//���ݿ��û���
	String user = DBProperty.getName();
	//���ݿ�����
	String password = DBProperty.getPassword();
	//�������ݿ����ȡ��һ�����ݿ�����
	conn = DriverManager.getConnection(url, user, password);
	return conn;
	}
	
	/**
	 * ϵͳ��ʼ��ʱ���������ݿ����ӳ�
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
		logger.info("DataBase Connection Pool Init������");
	}
	
	/**
	 * �����ݿ����ӳ��л�ȡ���ӣ�������ӳ���û�о��´���һ������
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
	 * �������ݿ����ӣ������ӳ��е������������趨ֵ����������ӽ����ر�
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
     * �ر�PreparedStatement
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
