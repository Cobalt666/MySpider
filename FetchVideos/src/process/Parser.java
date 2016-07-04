package process;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dao.VideoDao;
import bean.Config;
import bean.Pattern;
import bean.Video;
import filter.BloomFilter;

public class Parser implements Runnable {

	// 配置信息
	private static Config config = new Config();
	private static Pattern pattern = new Pattern();
	// 去重
	private static BloomFilter urlFilter = new BloomFilter();
	private static BloomFilter videoFilter = new BloomFilter();
	// Update数据库
	private static VideoDao videoDao = new VideoDao();
	// 存放Url
	private static ArrayList<String> waitUrls = new ArrayList<String>();
	private static ArrayList<String> enterUrls = new ArrayList<String>();
	private static int videoCount = 0;
	// 线程信号量，等待线程数
	public static final Object signal = new Object();
	private static int waitCount = 0;

	// 初始化
	public void init() {
		System.out.println("<---loading--->");
		// bloomfilter准备
		urlFilter.init();
		videoFilter.init();
		// 配置导入
		ConfigLoader configLoader = new ConfigLoader();
		config = configLoader.loadCrawlerConfig(1);
		pattern = configLoader.loadPattern(0);
		// 添加第一批url：从首页拿到网站分类入口
		// addAUrl(config.getEnterUrl());
		setEnterUrlsbyNav(config.getEnterUrl());
	}

	@Override
	public void run() {
		while (true) {
			String wait = getAWaitUrl();
			if (wait != null) {
				System.out.println("adding waiting list");
				setEnterUrls(wait);
			} else {
				System.out.println("waiting list is empty !");
			}
			String enter = getAEnterUrl();
			if (enter != null) {
				System.out.println("parsing: " + enterUrls.size()
						+ " left, now is : " + enter);
				parse(enter);
			}
			if (wait == null && enter == null) {
				waitCurrentThread();
			}
		}
	}

	public void waitCurrentThread() {
		synchronized (signal) {
			try {
				waitCount++;
				System.out.println("wait thread count: " + waitCount);
				signal.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// 直接从主站分析出每个分类的入口
	public void setEnterUrlsbyNav(String homepage) {
		try {
			Document doc = Jsoup
					.connect(homepage)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.timeout(10000).get();
			Elements es = doc.select(pattern.getEnter());
			for (Element e : es) {
				String linkHref = e.attr("abs:href");
				System.out.println("added category url: " + linkHref);
				addAUrl(linkHref);
				if (waitCount > 0) {
					synchronized (signal) {
						waitCount--;
						signal.notify();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 从主站的每个“更多”链接进入：http://www.bilibili.com/ | div.b-link-more
	public void setEnterUrls(String url) {
		try {
			Document doc = Jsoup
					.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.timeout(10000).get();
			Elements es = doc.select(pattern.getMore());
			for (Element e : es) {
				String linkHref = e.childNode(0).attr("abs:href");
				addAUrl(linkHref);
				if (waitCount > 0) {
					synchronized (signal) {
						waitCount--;
						signal.notify();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 判断是否进入内容界面
	public boolean hasContent(String url) {
		boolean b = false;
		try {
			Document doc = Jsoup
					.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.timeout(10000).get();
			if (doc.select(pattern.getList()).isEmpty()) {
				b = false;
			} else {
				b = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	// 判断是否进入错误页面，如登录页面
	public boolean isDead(String url) {
		boolean b = false;
		try {
			Document doc = Jsoup
					.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.timeout(10000).get();
			if (doc.select(pattern.getList()).isEmpty()
					|| doc.select(pattern.getMore()).isEmpty()) {
				b = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	public synchronized void parse(String url) {
		parseContent(url, pattern.getItem());
	}

	// div.l-item
	public void parseContent(String url, String cssQuery) {
		Elements es = new Elements();
		try {
			Document doc = Jsoup
					.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.timeout(10000).get();
			System.out.println("parseContent");
			es = doc.select(cssQuery);
			for (Element e : es) {
				if (e != null)
					parseAnElement(e);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 一个item
	public void parseAnElement(Element e) {
		System.out.println("parseAnElement");
		Video v = new Video();
		v.setTitle(getLegalValue(e, "Title"));
		v.setAuthor(getLegalValue(e, "Author"));
		v.setIntro(getLegalValue(e, "Intro"));
		v.setWatchCount(turnToInt(getLegalValue(e, "WatchCount")));
		v.setCollectCount(turnToInt(getLegalValue(e, "CollectCount")));
		v.setDanmakuCount(turnToInt(getLegalValue(e, "DanmakuCount")));
		v.setCreateTime(getLegalValue(e, "CreateTime"));
		v.setUrl(e.select(pattern.getUrl()).attr("abs:href"));
		addAVideo(v);
	}

	public String getLegalValue(Element e, String name) {
		String value = "";
		try {
			Class<? extends Pattern> patternClass = pattern.getClass();
			Method m;
			m = patternClass.getMethod("get" + name);
			String result = m.invoke(pattern).toString();
			if (result != "null") {
				if (!e.select(result).isEmpty()) {
					value = e.select(result).text();
				}
			}
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
		return value;
	}

	public int turnToInt(String s) {
		for (int i = s.length(); --i >= 0;) {
			if (!Character.isDigit(s.charAt(i))) {
				return 0;
			}
		}
		return Integer.parseInt(s);
	}

	public void addAVideo(Video v) {
		if (!videoFilter.isExit(v.getUrl())) {
			videoFilter.add(v.getUrl());
			System.out.println(v.getUrl());
			videoDao.add(v);
			videoCount++;
			System.out.println("added  " + videoCount + " videos");
		}
	}

	// 拿到一个新的链接，检查是否需要再分析链接，需要的先放入wait队列，不需要则直接放入enterUrls
	public synchronized void addAUrl(String url) {
		if (!urlFilter.isExit(url.toLowerCase())) {
			urlFilter.add(url.toLowerCase());
			if (hasContent(url)) {
				addEnterpages(url);
			} else if (!isDead(url)) {
				waitUrls.add(url);
			}
		}
	}

	public void addEnterpages(String startPage) {
		PageLoader pl = new PageLoader();
		ArrayList<String> pages = new ArrayList<String>();
		pages = pl.getMorePages(startPage, pattern.getId());
		for (int i = 0; i < pages.size(); i++) {
			enterUrls.add(pages.get(i));
		}
	}

	// 拿到一个需要接着分析链接的url
	public synchronized String getAWaitUrl() {
		if (!waitUrls.isEmpty()) {
			String url;
			url = waitUrls.get(0);
			waitUrls.remove(0);
			return url;
		} else
			return null;
	}

	// 拿到一个需要接着分析链接的url
	public synchronized String getAEnterUrl() {
		if (!enterUrls.isEmpty()) {
			String url;
			url = enterUrls.get(0);
			enterUrls.remove(0);
			return url;
		} else
			return null;
	}
}
