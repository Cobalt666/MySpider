package test;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filter.BloomFilter;

public class TestSimplePage implements Runnable {
	private static Document doc = null;
	private static String url = "http://www.bilibili.com/video/bangumi.html";

	public static void main(String args[]) {
		/*waitLinks.add(url);
		TestSimplePage sp = new TestSimplePage();
		while (!waitLinks.isEmpty()||waitLinks.size()==50) {
			new Thread(sp).start();
		}*/
		TestSimplePage sp = new TestSimplePage();
		sp.run();
	}

	/*private static void removeRepeatedUrls(List<String> wl) {
		BloomFilter bf = new BloomFilter();
		bf.init();
		for (String l : wl) {
			if (!bf.isExit(l))
				bf.add(l);
		}
		bf.clear();
	}*/

	private static void getSubLinks(String url) {
		try {
			System.out.println("开始调用getSubLinks方法");
			BloomFilter bloomFilter = new BloomFilter();
			int count = 1;
			bloomFilter.init();
			doc = Jsoup.connect(url).timeout(5000).get();
			Elements es = doc.select("div.b-link-more");
			for (Element e : es) {
				String linkHref = e.childNode(0).attr("abs:href");
				String linkText = e.text();
				if(linkHref==null)System.out.println("没有拿到地址");
				if (!bloomFilter.isExit(linkHref)) {
					System.out.println(count + ". linkText : " + linkText
							+ "\n linkHref : " + linkHref);
				}
				else
					System.out.println("重复地址");
				count++;
				bloomFilter.add(linkHref);
			}
			bloomFilter.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		getSubLinks(url);
	}

}
