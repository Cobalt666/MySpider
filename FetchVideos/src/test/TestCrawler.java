package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import process.ConfigLoader;
import dao.WebDao;
import filter.BloomFilter;
import bean.Config;
import bean.Web;

public class TestCrawler implements Runnable {
	public static final Object signal = new Object();
	private static int waitCount = 0;
	private static Config config = new Config();
	private static WebDao webDao = new WebDao();
	private static BloomFilter bloomFilter = new BloomFilter();
	private static ArrayList<String> waitUrl = new ArrayList<String>();
	private static HashMap<String, Integer> depthMap = new HashMap<String, Integer>();
	private static ArrayList<Web> webList = new ArrayList<Web>();
	
	public void init(){
		ConfigLoader configLoader = new ConfigLoader();
		config = configLoader.loadCrawlerConfig(0);
		bloomFilter.init();
		addAUrl(config.getDescription(), config.getEnterUrl(), 1);
	}
	
	public void run() {
		while (true) {
			String url = getAUrl();

			if (url != null) {
				int d = depthMap.get(url).intValue();
				System.out.println(Thread.currentThread().getName()
						+ "爬取成功，深度" + d);
				if (d < config.getCrawlerDepth())
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
				waitCount ++;
				signal.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

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
		if (webList.size() > config.getCacheCount() - 1) {
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
	
	public void changed(){
		System.out.println("changed");
	}
}
