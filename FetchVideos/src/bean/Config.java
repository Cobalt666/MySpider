package bean;

public class Config {
	private int id;
	private String enterUrl;
	private String description;
	private int threadCount;
	private int cacheCount;
	private int crawlerDepth;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEnterUrl() {
		return enterUrl;
	}
	public void setEnterUrl(String enterUrl) {
		this.enterUrl = enterUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	public int getCacheCount() {
		return cacheCount;
	}
	public void setCacheCount(int cacheCount) {
		this.cacheCount = cacheCount;
	}
	public int getCrawlerDepth() {
		return crawlerDepth;
	}
	public void setCrawlerDepth(int crawlerDepth) {
		this.crawlerDepth = crawlerDepth;
	}
	
}
