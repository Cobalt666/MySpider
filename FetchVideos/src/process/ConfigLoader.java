package process;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bean.Config;
import bean.Pattern;

public class ConfigLoader {
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory
			.newInstance();

	public Config loadCrawlerConfig(int index) {
		Config config = new Config();
		Document document = parse("config/config.xml");
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("crawler");
		if (nodeList != null) {
			Element child = (Element) nodeList.item(index);
			String id = child.getAttribute("id");
			Node enterUrl = child.getElementsByTagName("enterUrl").item(0).getFirstChild();
			Node description = child.getElementsByTagName("description").item(0).getFirstChild();
			Node threadCount = child.getElementsByTagName("threadCount").item(0).getFirstChild();
			Node cacheCount = child.getElementsByTagName("cacheCount").item(0).getFirstChild();
			Node crawlerDepth = child.getElementsByTagName("crawlerDepth").item(0).getFirstChild();
			config.setId(Integer.parseInt(id));
			config.setEnterUrl(enterUrl.getNodeValue());
			config.setDescription(description.getNodeValue());
			config.setThreadCount(Integer.parseInt(threadCount.getNodeValue()));
			config.setCacheCount(Integer.parseInt(cacheCount.getNodeValue()));
			config.setCrawlerDepth(Integer.parseInt(crawlerDepth.getNodeValue()));
		}
		return config;
	}
	
	public Pattern loadPattern(int index){
		Pattern pattern = new Pattern();
		Document document = parse("config/pattern.xml");
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("video");
		if (nodeList != null) {
			Element child = (Element) nodeList.item(index);
			String id = child.getAttribute("id");
			Node info = child.getElementsByTagName("info").item(0).getFirstChild();
			Node enter = child.getElementsByTagName("enter").item(0).getFirstChild();
			Node more = child.getElementsByTagName("more").item(0).getFirstChild();
			Node list = child.getElementsByTagName("list").item(0).getFirstChild();
			Node item = child.getElementsByTagName("item").item(0).getFirstChild();
			Node pagebox = child.getElementsByTagName("pagebox").item(0).getFirstChild();
			Node pageCount = child.getElementsByTagName("pageCount").item(0).getFirstChild();
			Node title = child.getElementsByTagName("title").item(0).getFirstChild();
			Node author = child.getElementsByTagName("author").item(0).getFirstChild();
			Node intro = child.getElementsByTagName("intro").item(0).getFirstChild();
			Node watchCount = child.getElementsByTagName("watchCount").item(0).getFirstChild();
			Node collectCount = child.getElementsByTagName("collectCount").item(0).getFirstChild();
			Node danmakuCount = child.getElementsByTagName("danmakuCount").item(0).getFirstChild();
			Node createTime = child.getElementsByTagName("createTime").item(0).getFirstChild();
			Node url = child.getElementsByTagName("url").item(0).getFirstChild();
			pattern.setId(Integer.parseInt(id));
			pattern.setInfo(info.getNodeValue());
			pattern.setEnter(enter.getNodeValue());
			pattern.setMore(more.getNodeValue());
			pattern.setList(list.getNodeValue());
			pattern.setItem(item.getNodeValue());
			pattern.setPagebox(pagebox.getNodeValue());
			pattern.setPageCount(pageCount.getNodeValue());
			pattern.setTitle(title.getNodeValue());
			pattern.setAuthor(author.getNodeValue());
			pattern.setIntro(intro.getNodeValue());
			pattern.setWatchCount(watchCount.getNodeValue());
			pattern.setCollectCount(collectCount.getNodeValue());
			pattern.setDanmakuCount(danmakuCount.getNodeValue());
			pattern.setCreateTime(createTime.getNodeValue());
			pattern.setUrl(url.getNodeValue());
		}
		return pattern;
	}

	public Document parse(String filePath) {
		Document document = null;
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			document = builder.parse(new File(filePath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}
}
