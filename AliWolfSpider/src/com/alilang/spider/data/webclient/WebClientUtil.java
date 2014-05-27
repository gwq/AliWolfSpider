package com.alilang.spider.data.webclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 
 * @author wuqiang.gwq
 *
 */
public class WebClientUtil {
	
	private static final Logger logger = Logger.getLogger(WebClientUtil.class);
	public static String Post = "POST";
	public static String Get = "GET";
	public static boolean setCookie = true;
	public static String cookie;
	public static String loginFailCookie = "gsid_CTandWM=deleted";
	
	/**
	 * 
	 * @param reqURL
	 * @param params
	 * @param headparams
	 * @param method
	 * @return
	 */
	public static String sendRequest(String reqURL, Map<String, String> params, Map<String, String> headparams, String method) {
		return sendRequest(reqURL, params,headparams, method,!setCookie);
	}

	
	/**
	 * 通过HttpURLConnection发送请求
	 * @param reqURL
	 * @param params
	 * @param headparams
	 * @param method
	 * @return
	 */
	public static String sendRequest(String reqURL, Map<String, String> params, Map<String, String> headparams, String method,boolean setcookie){
        String responseContent = "";                     //响应内容 
        
//        System.setProperty("http.proxyHost", "122.117.162.69");
//        System.setProperty("http.proxyPort", "8080");

        HttpURLConnection hul = null;
		URL url;
		try {
			url = new URL(reqURL);//实例URL
			hul = (HttpURLConnection)url.openConnection();//打开连接
			hul.setConnectTimeout(10000);
			hul.setReadTimeout(10*1000);
			if (params != null)
				hul.setDoOutput(true);
			else
				hul.setDoOutput(false);//当上传文件和参数时，将其设置为true
			hul.setDoInput(true);
			hul.setUseCaches(false);//无缓存
			hul.setRequestMethod(method);//设置请求方法
			hul.setInstanceFollowRedirects(false);//不允许页面进行自动跳转 
			hul.setRequestProperty("User-Agent", "Mozilla/5.0");//在连接前加入User-agent，否则连接时可能会引起403错误
			
			if(headparams != null){                               //ip头参数设置
            	for(Map.Entry<String,String> entry : headparams.entrySet()){
            		hul.setRequestProperty(entry.getKey(), entry.getValue());
            	}
            }
			
			hul.connect(); //建立连接
			if(params != null){  //设置参数
				StringBuilder sb = new StringBuilder();
				for(Map.Entry<String,String> entry : params.entrySet()){  
	                  sb.append(URLEncode(entry.getKey()));
	                  sb.append("=");
	                  sb.append(URLEncode(entry.getValue()));
	                  sb.append("&");
	            }
				sb.deleteCharAt(sb.length() - 1);
				DataOutputStream out = new DataOutputStream(hul.getOutputStream());
				out.writeBytes(sb.toString());
				out.flush();
				out.close();
			}
			
			StringBuilder respStr = new StringBuilder();
			String line = "";
			
			if(!setcookie){
				int responseCode = hul.getResponseCode();
				if (!(responseCode==200)){
					logger.error(hul.getURL().toString());
					logger.error("HTTP Response Code:"+responseCode);
					return null;
				}
				BufferedReader reader = new BufferedReader(new InputStreamReader(hul.getInputStream(),"UTF-8"));
				while((line = reader.readLine())!=null){//遍历信息内容
					respStr.append(line);
				}
				reader.close();		
				responseContent = respStr.toString();
			}
			logger.debug(hul.getResponseCode());
			logger.debug(responseContent);
			if (setcookie){
				boolean set_cookie_exit = false;
				boolean gsid_CTandWM_exit = false;
				/*
				 * heard中有多个Set-Cookie，因此不能用hul.getHeader("Set-Cookie")来取值
				 */
				for (int i = 0;; i++) {
					if(hul.getHeaderFieldKey(i) != null && hul.getHeaderFieldKey(i).contains("Set-Cookie")){
						set_cookie_exit = true;
						if (hul.getHeaderField(i) != null && hul.getHeaderField(i).contains("gsid_CTandWM")){
							gsid_CTandWM_exit = true;
							Pattern p =Pattern.compile(".*?(gsid_CTandWM=.*?);.*?");
							Matcher m = p.matcher(hul.getHeaderField(i));
							while (m.find()){
								cookie = m.group(1);
							}
							if (cookie.equals(loginFailCookie)){
								logger.error("微博账号或密码错误！！！");
								return null;
							}
							break;
						}
					}
				    if (hul.getHeaderFieldKey(i) == null && hul.getHeaderField(i) == null) {
				        break;
				      }
				    }
				if (!set_cookie_exit){
					logger.error("微博登录set_cookie解析错误！！！");
				}
				if (!gsid_CTandWM_exit){
					logger.error("微博登录gsid_CTandWM解析错误！！！");
				}
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("服务器连接问题！！");
			e.printStackTrace();
		} finally{
			if(hul != null)
				hul.disconnect();
			hul = null;
		}
		
        return responseContent;
	}
	
	/**
	 * URL编码工具
	 * @param str
	 * @return
	 */
	public static String URLEncode(String str){
		try {
			str = URLEncoder.encode(str,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * URL解码工具
	 * @param str
	 * @return
	 */
	public static String URLDecode(String str){
		try {
			str = URLDecoder.decode(str,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

}
