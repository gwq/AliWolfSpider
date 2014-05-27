package com.alilang.spider.data.webclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author wuqiang.gwq
 * 
 */
public class WeiBoUtil {

	private static final Logger logger = Logger.getLogger(WeiBoUtil.class);
	private String usedDateFormtReg = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}";
	private String thisYearDateFormtReg = "^\\d{2}\\D\\d{2}\\D \\d{2}:\\d{2}";
	private String todayDateFormtReg = "^\\u4eca\\u5929.*"; // �����족��unicode��
	private String minTimeFormtReg = "^(\\d+?)\\u5206\\u949f\\u524d.*";
	private String commonDateFormtReg = ".*? \\d{2}:\\d{2}";
	private String inputDateStrReg = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$";

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat esdf = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
	
	public static LinkedList<String> friendsList = new LinkedList<String>();
	
	/**
	 * ��ȡ�����б�
	 * @return
	 */
	public LinkedList<String> getFriendsByAPI(){
		StringBuilder sb = new StringBuilder();
        String screenname = "";
        try {
			screenname = URLEncoder.encode(AccountProperty.getScreenName(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		sb.append("https://api.weibo.com/2/friendships/friends.json");// ��ȡ��ǰ�û�����
		sb.append("?");
		sb.append("screen_name=" + screenname);
		sb.append("&");
		sb.append("access_token=" + AccountProperty.getToken());
		sb.append("&");
		sb.append("page=1"); // ҳ��
		sb.append("&");
		sb.append("count=200"); // ÿҳ�ļ�¼��
		String result = WebClientUtil.sendRequest(sb.toString(), null, null,
				WebClientUtil.Get);
		JSONObject jo = JSONObject.fromObject(result);
		LinkedList<String> list = new LinkedList<String>();
		JSONArray userArray = jo.getJSONArray("users");
		Iterator<?> iterUser = userArray.iterator();
		while (iterUser.hasNext()){
			JSONObject userItem = (JSONObject) iterUser.next();
			list.add(userItem.getString("screen_name"));
		}
		
		return list;
	}

	/**
	 * ͨ������API��ȡ���� list�ĵ�λΪһ��΢��(String[])
	 * string[0]:΢������;string[1]:΢������;string[2]:΢������ʱ��
	 * 
	 * @param page
	 * @param dateStr
	 * @return
	 */
	
	public LinkedList<String[]> weiboHomePageByAPI(int page, String dateStr) {
		StringBuilder sb = new StringBuilder();
		long limitTime = 0L;
		if (dateStr != null) {
			if (!dateStr.matches(inputDateStrReg)){
				logger.error("�����ʱ�����ʽ����(" + dateStr
						+ ")����ʽ����Ϊ yyyy-MM-dd HH:mm");
				return null;
			}
			try {
				limitTime = (msdf.parse(dateStr)).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		

		// sb.append("https://api.weibo.com/2/statuses/public_timeline.json");//��ȡ����΢��������200��
		// sb.append("?");
		// sb.append("access_token="+AccountProperty.getToken());
		// sb.append("&");
		// sb.append("_t=0");

		sb.append("https://api.weibo.com/2/statuses/home_timeline.json");// ��ȡ��ǰ�û���ע��΢�����ݣ���ҳ���100��
		sb.append("?");
		sb.append("access_token=" + AccountProperty.getToken());
		sb.append("&");
		sb.append("page=" + page); // ҳ��
		sb.append("&");
		sb.append("count=100"); // ÿҳ�ļ�¼��

		String result = WebClientUtil.sendRequest(sb.toString(), null, null,
				WebClientUtil.Get);
		JSONObject jo = JSONObject.fromObject(result);
		// System.out.println(result);
		LinkedList<String[]> list = new LinkedList<String[]>();

		JSONArray weiboArray = jo.getJSONArray("statuses");
		Iterator<?> iterWeibo = weiboArray.iterator();
		LinkedList<Object> errorlist = new LinkedList<Object>();
		while (iterWeibo.hasNext()) {
			String[] sArray = new String[3];
			JSONObject weiboItem = (JSONObject) iterWeibo.next();
			String weiboTime = weiboItem.getString("created_at");
			String weiboText = weiboItem.getString("text");// ΢������
			JSONObject user = weiboItem.getJSONObject("user");
			String username = user.getString("screen_name");
			float messy = isMessyCode(weiboText);// �ж��Ƿ�������
			logger.debug("=======================================");
			logger.debug(username);
			logger.debug(weiboText);
			logger.debug("�����ʣ�" + messy);
			Date cdate = new Date();
			try {
				cdate = esdf.parse(weiboTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if (limitTime > 0){
				long ctime = cdate.getTime();
				if (limitTime > ctime)
					break;
			}
			
			weiboTime = msdf.format(cdate);
			logger.debug(weiboTime);
			
			sArray[0] = username;
			sArray[1] = weiboText;
			sArray[2] = weiboTime;
			if (messy == 0f) {
				list.add(sArray);
			}
			else{
				errorlist.add(sArray);
			}
		}
		
		for(Object o : errorlist){
			logger.debug("#####################");
			logger.debug(((String[])o)[0]);
			logger.debug(((String[])o)[1]);
			logger.debug(((String[])o)[2]);
		}
		logger.debug("�ַ����⣺"+errorlist.size());
		return list;
	}

	/**
	 * ��ȡ�û���Ϣ
	 * 
	 * @param username
	 * @return �û�΢�����Ի�����
	 */
	public String getWeiboUserInfo(String username) {
		username = WebClientUtil.URLEncode(username);
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.weibo.com/2/users/show.json");
		sb.append("?");
		sb.append("access_token=" + AccountProperty.getToken());
		sb.append("&");
		sb.append("screen_name=" + username);
		String result = WebClientUtil.sendRequest(sb.toString(), null, null,
				WebClientUtil.Get);
		logger.debug(result);
		if (result != null) {
			JSONObject jo = JSONObject.fromObject(result);
			return jo.getString("domain");
		} else {
			logger.error("���û�(" + username + ")�����ڣ���");
			return null;
		}
	}

	public LinkedList<String[]> getWeiboContent(String username, int page) {
		return getWeiboContent(username, page, null);
	}

	/**
	 * ��ȡweibo.cn�ϵ�����,��ȡ΢����Ϣ list�ĵ�λΪһ��΢��(String[])
	 * string[0]:΢������;string[1]:΢������;string[2]:΢������ʱ��
	 * 
	 * @param username
	 *            ĳ��΢���ǳƣ�""�����ȡ��ҳ��΢��
	 * @param page
	 *            ��ȡ��ҳ��
	 * @param dateStrlimit
	 *            ʱ����� ��ʽ����Ϊyyyy-MM-dd HH:mm��ֻ��ȡ��ʱ��֮ǰ��΢����/NULL
	 */
	public LinkedList<String[]> getWeiboContent(String username, int page,
			String dateStrlimit) {
		boolean homePage = true;
		if (WebClientUtil.cookie == null)
			this.weiboLogin();
		if (WebClientUtil.cookie.equals(WebClientUtil.loginFailCookie)) { // �����¼ʧ�ܣ���Ȼû�л��cookie�����˳�
			return null;
		}
		if (dateStrlimit != null && !dateStrlimit.matches(inputDateStrReg)) {
			logger.error("�����ʱ�����ʽ����(" + dateStrlimit
					+ ")����ʽ����Ϊ yyyy-MM-dd HH:mm");
			return null;
		}
		String domain = "";
		if (username != null && !username.equals("")) {
			domain = getWeiboUserInfo(username);
			if (domain == null) // û�и����ǳ��ҵ��û�
				return null;
			homePage = false;
		}
		Map<String, String> header = new HashMap<String, String>();
		header.put("Cookie", WebClientUtil.cookie);

		String result = WebClientUtil.sendRequest("http://weibo.cn/" + domain
				+ "?page=" + page + "&vt=4", null, header, WebClientUtil.Get);
		LinkedList<String[]> weibolist;

		if (homePage)
			weibolist = getHomePageWeiboContent(page, dateStrlimit, result);
		else
			weibolist = getPersonalWeiboContent(page, dateStrlimit, result);
		return weibolist;
	}

	/**
	 * ����ĳ�˵�΢����Ϣ
	 * 
	 * @param page
	 * @param dateStrlimit
	 * @param pageContent
	 * @return
	 */
	private LinkedList<String[]> getPersonalWeiboContent(int page,
			String dateStrlimit, String pageContent) {
		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);

		long currentTime = cal.getTimeInMillis();// ��ǰʱ���longֵ
		String today = sdf.format(currentTime);

		Pattern p = Pattern.compile(commonDateFormtReg + "|" + minTimeFormtReg);// ƥ��΢������ʱ��
		Pattern minutep = Pattern.compile(minTimeFormtReg);

		Document doc = Jsoup.parse(pageContent);
		Elements eles = doc.select("div.c>div>span.ctt,div.c>div>span.ct");

		LinkedList<String[]> weibolist = new LinkedList<String[]>();
		int index = 0;
		String[] item = new String[3];
		int top = 1;// ����[�ö�],topΪ[�ö�]����
		for (Element e : eles) {
			if (index == 0) {
				item = new String[3];
				item[1] = e.text();
				logger.debug(item[1]);// ΢������

			}
			if (index == 1) {
				Matcher matcher = p.matcher(e.text());
				while (matcher.find()) {
					item[2] = weiboDateFormat(matcher.group(0), y, today,
							minutep);
					logger.debug(item[2]);// ΢������ʱ��
				}

				if (dateStrlimit != null) {
					if (dateStrToLong(dateStrlimit) > dateStrToLong(item[2])) {
						logger.debug("===========================");
						if (page == 1 && top > 0){ 
							Elements es = doc.select("div.c>div>span.kt");
							if(es.size() > 0 && es.get(0).text().equals("\u7f6e\u9876")){//"�ö�"
								top--;
								index = (index + 1) % 2;
								continue;
							}
							else
								break;
						} else
							break;
					}
				}
				weibolist.add(item);
			}
			index = (index + 1) % 2;
		}
		return weibolist;
	}

	/**
	 * ������ҳ΢������
	 * 
	 * @param page
	 * @param dateStrlimit
	 * @param pageContent
	 * @return
	 */
	private LinkedList<String[]> getHomePageWeiboContent(int page,
			String dateStrlimit, String pageContent) {
		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);

		long currentTime = cal.getTimeInMillis();// ��ǰʱ���longֵ
		String today = sdf.format(currentTime);

		Pattern p = Pattern.compile(commonDateFormtReg + "|" + minTimeFormtReg);// ƥ��΢������ʱ��
		Pattern minutep = Pattern.compile(minTimeFormtReg);

		Document doc = Jsoup.parse(pageContent);
		Elements eles = doc
				.select("div.c>div>a.nk,div.c>div>span.ctt,div.c>div>span.ct");

		LinkedList<String[]> weibolist = new LinkedList<String[]>();
		int index = 0;
		String[] item = new String[3];
		int top = 1;// ����[�ö�],topΪ[�ö�]����
		for (Element e : eles) {
			if (index == 0) {
				item = new String[3];
				item[0] = e.text();
				logger.debug(item[0]);// ΢������

			}
			if (index == 1) {
				item[1] = e.text();
				logger.debug(item[1]);// ΢������

			}
			if (index == 2) {
				Matcher matcher = p.matcher(e.text());
				while (matcher.find()) {
					item[2] = weiboDateFormat(matcher.group(0), y, today,
							minutep);
					logger.debug(item[2]);// ΢������ʱ��
				}

				if (dateStrlimit != null) {
					if (dateStrToLong(dateStrlimit) > dateStrToLong(item[2])) {
						logger.debug("===========================");
						if (page == 1 && top > 0){ 
							Elements es = doc.select("div.c>div>span.kt");
							if(es.size() > 0 && es.get(0).text().equals("\u7f6e\u9876")){//"�ö�"
								top--;
								index = (index + 1) % 2;
								continue;
							}
							else
								break;
						} else
							break;
					}
				}
				weibolist.add(item);
			}
			index = (index + 1) % 3; //
		}
		return weibolist;
	}

	/**
	 * ������ȡ��΢��ʱ���ʽ
	 * 
	 * @param dateStr
	 * @param y
	 * @param today
	 * @return
	 */
	private String weiboDateFormat(String dateStr, int y, String today,
			Pattern minutep) {
		if (dateStr.matches(usedDateFormtReg)) {
			return dateStr;
		}
		if (dateStr.matches(thisYearDateFormtReg)) {
			StringBuilder sb = new StringBuilder();
			sb.append(y);
			sb.append('-');
			char[] cs = dateStr.toCharArray();
			for (int i = 0; i < cs.length; i++) {
				if (i == 2) {
					sb.append('-');
					continue;
				}
				if (i == 5) {
					continue;
				}
				sb.append(cs[i]);
			}
			return sb.toString();
		}
		if (dateStr.matches(todayDateFormtReg)) {
			return dateStr.replaceAll("\\u4eca\\u5929", today);
		}
		if (dateStr.matches(minTimeFormtReg)) {
			Matcher m = minutep.matcher(dateStr);
			long min = 0L;
			while (m.find()) {
				min = Long.valueOf(m.group(1));
			}

			long weibotime = System.currentTimeMillis() - min * 60000;
			return msdf.format(weibotime);
		}
		return dateStr;
	}

	private long dateStrToLong(String dateStr) {
		long o = 0L;
		try {
			o = msdf.parse(dateStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * ģ���½����ȡ��Ȩcookie
	 */
	public void weiboLogin() {

		String loginUrl = "http://login.weibo.cn/login/?ns=1&revalid=2&backURL=http://weibo.cn/?s2w=login&backTitle=΢��&vt=4";
		String result = WebClientUtil.sendRequest(loginUrl, null, null,
				WebClientUtil.Get);
		Map<String, String> params = new HashMap<String, String>();
		Document doc = Jsoup.parse(result);
		Elements eles = doc.select("input");
		for (Element e : eles) {
			String value = WebClientUtil.URLDecode(e.attr("value"));
			params.put(e.attr("name"), value);
			if (e.attr("name").contains("password"))
				params.put(e.attr("name"), AccountProperty.getAccountPasswd());
		}

		params.put("mobile", AccountProperty.getAccountName());
		params.put("remember", "on");

		loginUrl = "http://login.weibo.cn/login/?rand=1164771177&backURL=http%3A%2F%2Fweibo.cn%2F%3Fs2w%3Dlogin&backTitle=%E5%BE%AE%E5%8D%9A&vt=4&revalid=2&ns=1";
		result = WebClientUtil.sendRequest(loginUrl, params, null,
				WebClientUtil.Post, WebClientUtil.setCookie);

	}

	/**
	 * �ж��Ƿ�Ϊ���ĺ��ֺͷ���
	 * 
	 * @return
	 */
	// GENERAL_PUNCTUATION �ж����ĵġ��� //
	// CJK_SYMBOLS_AND_PUNCTUATION �ж����� �ġ��� /
	// HALFWIDTH_AND_FULLWIDTH_FORMS �ж����ĵģ���
	private static boolean isChinese(char c) {
		String character = "~!@'#��$%^&_*��()_+`-=���_����{}|��?<>";
		if (character.contains(String.valueOf(c))){
			return true;
		}
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
		        || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.VERTICAL_FORMS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * �ж��Ƿ�Ϊ����,����������
	 * 
	 * @return
	 */
	private static float isMessyCode(String strName) {

		Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");//ƥ��Ӣ�ı��
		 
		char[] ch = temp.trim().toCharArray();
		float chLength = ch.length;
		float count = 0;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!Character.isLetterOrDigit(c)) { // ȷ��ָ���ַ��Ƿ�Ϊ��ĸ������

				if (!isChinese(c)) {
					count = count + 1;
				}
			}
		}
		float result = count / chLength;
		return result;

	}
	

}
