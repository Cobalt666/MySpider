package process;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import bean.Config;

public class Crawler {
	
	private static Config config = new Config();
	
	public void init(){
		ConfigLoader configLoader = new ConfigLoader();
		config = configLoader.loadCrawlerConfig(1);
	}
	
	public static void main(String args[]){
		Crawler crawler = new Crawler();
		crawler.init();//载入配置
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getThreadCount());
		
		Parser parser = new Parser();
		parser.init();
		for (int i = 0; i < config.getThreadCount(); i++) {
			executor.execute(parser);
		}
	}
	
}
