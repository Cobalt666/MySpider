package test;

import java.io.IOException;
import java.lang.reflect.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bean.Pattern;

public class TestCSSQuery {
	private static String cssQuery = "div:contains(yk-append) > a[href] || div:contains(yk-extend) > a[href]";
	private static String regex = "(yk-append)|(yk-extend)";

	public static void main(String args[]) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		/*
		 * try { Document doc = Jsoup.connect(
		 * "http://tv.youku.com/hk?from=y1.3-tv-grid-1007-9910.86883-86880.0-1"
		 * ).timeout(6000).get(); Elements elements = doc.select(cssQuery);
		 * for(Element e : elements) { System.out.println(e.attr("abs:href")); }
		 * } catch (IOException e) { e.printStackTrace(); }
		 */
		/*
		 * String s="yk-append"; String s1="asfagfagyk-appenddsyk-extendfsaf";
		 * String s2="yk-extend"; System.out.println(s.matches(regex));
		 * System.out.println(s1.matches(regex));
		 * System.out.println(s2.matches(regex));
		 */

		Pattern p = new Pattern();
		p.setAuthor("a");
		Class<? extends Pattern> patternClass = p.getClass();

		Field[] fields = patternClass.getDeclaredFields();
		Method m = patternClass.getMethod("getAuthor");
		String result = m.invoke(p).toString();
		System.out.println(result);
		
	}
}
