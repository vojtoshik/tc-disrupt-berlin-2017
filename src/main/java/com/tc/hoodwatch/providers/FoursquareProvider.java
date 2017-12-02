package com.tc.hoodwatch.providers;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;

public class FoursquareProvider {
	public static void main(String[] args) throws IOException, URISyntaxException {
		try(
				CloseableHttpClient httpclient = HttpClients.createDefault();
		) {
			URIBuilder builder = new URIBuilder();
			builder = builder.setScheme("https").setHost("api.foursquare.com").setPath("/v2/venues/search");
			builder = builder.setParameter("client_id", getProperty("client_id"));
			builder = builder.setParameter("client_secret", getProperty("client_secret"));
			builder = builder.setParameter("v", "20171202");
			builder = builder.setParameter("ll", "50.45,30.523333");
			builder = builder.setParameter("intent", "browse");
			builder = builder.setParameter("radius", "1000");
			builder = builder.setParameter("categoryId", "4e0e22f5a56208c4ea9a85a0");


			HttpGet httpGet = new HttpGet(builder.build());
			try(
					CloseableHttpResponse response = httpclient.execute(httpGet);
			) {
				System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				try {
					// do something useful with the response body
					System.out.println(EntityUtils.toString(entity));
				}
				finally {
					// and ensure it is fully consumed
					EntityUtils.consume(entity);
				}
			}
		}

	}

	public static String getProperty(String key) {
		return getProperty(key, null);
	}

	public static String getProperty(String key, String defaultValue) {
		String r = System.getenv(key);
		if(r == null) {
			r = System.getProperty(key);
		}
		if(r == null) {
			r = defaultValue;
		}
		return r;
	}
}
