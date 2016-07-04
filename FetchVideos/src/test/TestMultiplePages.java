package test;

import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dao.WebDao;
import bean.Web;
import filter.BloomFilter;

public class TestMultiplePages implements Runnable {

	private static ArrayList<String> waitUrl = new ArrayList<String>();// 等待网页url
	private static HashMap<String, Integer> depthMap = new HashMap<String, Integer>();// 所有网页的深度映射url->depth

	private static int cacheCount = 100;// 一次写入数据库的条数
	private static int crawlerDepth = 5; // 爬虫深度
	private static int waitCount = 0; // 线程处于wait状态的数量
	public static final Object signal = new Object();// 信号量
	private static BloomFilter bloomFilter = new BloomFilter();
	private static WebDao webDao = new WebDao();
	private static ArrayList<Web> webList = new ArrayList<Web>();// 缓存
	
	public void init(){
		bloomFilter.init();
		addAUrl("cnblog", "http://www.cnblogs.com", 1);
	}
	
	public void run() {
		while (true) {
			String url = getAUrl();

			if (url != null) {
				int d = depthMap.get(url).intValue();
				System.out.println(Thread.currentThread().getName()
						+ "爬取成功，深度" + d);
				if (d < crawlerDepth)
					getSubUrls(url, d + 1);
				else
					waitCurrentThread();
			} else
				waitCurrentThread();
		}
	}


	public void waitCurrentThread() {
		synchronized (signal) {
			try {
				waitCount++;
				//System.out.println("有" + waitCount + "个线程在等待");
				signal.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// 包括：分析上下文，提取url，查重，加入等待队列，唤醒等待线程
	public void getSubUrls(String url, int d) {
		try {
			Document doc = Jsoup.connect(url).timeout(6000).get();
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				String linkHref = link.attr("abs:href").trim();
				String linkText = link.text();
				if (isAUrl(linkHref)) {
					if(!bloomFilter.isExit(linkHref.toLowerCase())){
						addAUrl(linkText, linkHref, d);
						if (waitCount > 0) {
							synchronized (signal) {
								waitCount--;
								signal.notify();
							}
						}
					}
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// !需要完善：正则表达式
	public boolean isAUrl(String url) {
		if (!url.isEmpty()) {
			if (url.startsWith("http://"))
				return true;
			else
				return false;
		} else
			return false;
	}

	public synchronized String getAUrl() {
		if (!waitUrl.isEmpty()) {
			String url;
			url = waitUrl.get(0);
			waitUrl.remove(0);
			return url;
		} else
			return null;
	}

	public synchronized void addAUrl(String name, String url, int d) {
		if (webList.size() > cacheCount - 1) {
			webDao.addUrls(webList);
			webList.removeAll(webList);
		}
		if (!bloomFilter.isExit(url.toLowerCase())) {
			Web web = new Web();
			waitUrl.add(url);
			depthMap.put(url, d);
			web.setName(name);
			web.setUrl(url);
			web.setDeep(d);
			webList.add(web);
			bloomFilter.add(url.toLowerCase());
		}
	}

}
