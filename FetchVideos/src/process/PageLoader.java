package process;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bean.Pattern;

public class PageLoader {
	ConfigLoader configLoader = new ConfigLoader();
	private static Pattern pattern = new Pattern();
	private static ArrayList<String> pages = new ArrayList<String>();
	
	public ArrayList<String> getMorePages(String startPage, int index){
		pattern = configLoader.loadPattern(index-1);
		if(index==1){
			getPagesFromBilibili(startPage);
		}
		/*else if(index==2){
			getPagesFromYouku(startPage);
		}*/
		return pages;
	}

	public void getPagesFromBilibili(String startPage) {
		try {
			int pageIndex = startPage.indexOf('1');
			int pageCount = 0;
			String value = "";
			Document doc = Jsoup.connect(startPage).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
		               .timeout(10000).get();
			Element element = doc.select(pattern.getPagebox()).get(0);
			value = element.select(pattern.getPageCount()).text();
			// 有些页数较少的页面，并没有最后一页这样的按钮，所以就取倒数第2个为最后一个链接，因为倒数第一个是“下一页”
			if (value.isEmpty()) {
				Elements es = element.select("a.p[href]");
				value = es.get(es.size() - 2).text();
			}
			pageCount = Integer.parseInt(value);
			for (int i = 0; i < pageCount; i++) {
				String s = startPage.substring(0, pageIndex) + (i + 1)
						+ ".html";
				pages.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*public void getPagesFromYouku(String startPage) {
		try {
			int pageIndex = 0;
			int pageCount = 0;
			String value = "";
			Document doc = Jsoup.connect(startPage).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
		               .timeout(10000).get();
			Element element = doc.select(pattern.getPagebox()).get(0);
			Element next = doc.select("li.next").get(0);
			Elements es = element.select("a.p[href]");
			pageIndex = next.attr("abs:href").indexOf(".html")
			value = es.get(es.size() - 2).text();
			pageCount = Integer.parseInt(value);
			for (int i = 0; i < pageCount; i++) {
				String s = startPage.substring(0, pageIndex) + (i + 1)
						+ ".html";
				pages.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
