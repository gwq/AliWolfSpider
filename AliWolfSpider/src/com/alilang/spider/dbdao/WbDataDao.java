package com.alilang.spider.dbdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.alilang.spider.data.webclient.WebClientUtil;
import com.alilang.spider.data.webclient.WeiBoUtil;
import com.alilang.spider.db.DBPool;
import com.alilang.spider.thread.GetWeiboDBThread;
import com.alilang.spider.thread.ThreadG;
import com.alilang.spider.thread.ThreadPool;
import com.alilang.spider.web.support.ContextloaderListener;

public class WbDataDao {
	
	private static final Logger logger = Logger.getLogger(WbDataDao.class);
	
	public void insertWeibo(int page){
		insertWeibo("",page,null,true);
	}
	
	public void insertWeibo(String username,int page,String dateStr){
		insertWeibo(username,page,dateStr,false);
	}
	
	public void insertWeibo(String username,int page){
		insertWeibo(username,page,null,false);
	}
	
	public void insertWeibo(String username,int page,boolean isUseAPI){
		insertWeibo(username,page,null,isUseAPI);
	}
	
	/**
	 * 
	 * @param username  用户的微博昵称，当为""时读取个人首页关注的人的微博
	 * @param page
	 * @param dateStr  时间戳，格式必须为yyyy-MM-dd HH:mm /null为不设时间戳
	 * @param isUseAPI 爬取方式 true:使用API;false:通过页面爬虫
	 */
	public void insertWeibo(String username,int page,String dateStr,boolean isUseAPI) {
		
		WeiBoUtil wb =new WeiBoUtil();
		LinkedList<String[]> list = null;
		if (!isUseAPI){
			list = wb.getWeiboContent(username, page,dateStr);
		}
		else{
			list = wb.weiboHomePageByAPI(page,dateStr);
		}
		
		if (list == null || list.size() == 0)
			return;
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");	
		Connection conn = DBPool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);//关闭自动提交
			ps = conn.prepareStatement("insert into wb_data(username,wb_create,content,gmt_create,gmt_modified)values(?,?,?,?,?)");
			String[] item = list.poll();
			while (item != null){
				logger.debug(item[1]);
				logger.debug(item[2]);
				java.sql.Timestamp weibodate = new java.sql.Timestamp((sdf1.parse(item[2])).getTime());
				java.sql.Timestamp dt = new java.sql.Timestamp(System.currentTimeMillis());
				if (username != null && !username.equals("")){
					ps.setString(1, username);
					logger.debug(username);
				}
				else{
					ps.setString(1, item[0]);
					logger.debug(item[0]);
					
				}
				ps.setTimestamp(2, weibodate);
				ps.setString(3, item[1]);
				ps.setTimestamp(4, dt);
				ps.setTimestamp(5, dt);
				ps.addBatch();
				item = list.poll();
			}
			ps.executeBatch();
			conn.commit();
			
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			DBPool.closePS(ps);
			DBPool.recycelConn(conn);
		}
	}
	
	@Test
	public void daoTest(){
		new ContextloaderListener().init();
		//insertWeibo(6);
		
		
	}
	
	

}
