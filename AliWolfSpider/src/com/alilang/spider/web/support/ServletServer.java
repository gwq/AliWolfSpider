package com.alilang.spider.web.support;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletServer extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7249899542931535270L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("servelt server : "+req.getRequestURI());
		responseFileFormat(resp, "≤‚ ‘Œƒº˛.txt", "≤‚ ‘ƒ⁄»›");
		
	}
	
	private void responseFileFormat(HttpServletResponse resp,String filename,String content) throws IOException{
		ServletOutputStream os = resp.getOutputStream();
		resp.reset();
		resp.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename,"utf-8"));
		resp.setContentType("application/x-msdownload");
		os.write(content.getBytes("UTF-8"));
		os.close();
	}

}
