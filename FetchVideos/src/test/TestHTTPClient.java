package test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class TestHTTPClient {

	public static void main(String args[]){
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		try {
			HttpGet httpGet = new HttpGet("http://www.open-open.com/jsoup/");
			CloseableHttpResponse response1 = httpClient.execute(httpGet);
			System.out.println();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
