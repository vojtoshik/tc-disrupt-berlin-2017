package com.tc.hoodwatch.providers;

import com.tc.hoodwatch.model.foursquare.Venue;
import com.tc.hoodwatch.model.foursquare.VenueCategory;
import com.tc.hoodwatch.model.foursquare.Yeah;
import com.tc.hoodwatch.util.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class FoursquareProvider {
	private static final Map<String, String> venueCategories;

	static {
		venueCategories = new HashMap<String, String>() {{
			put("arts_entertainment", "4d4b7104d754a06370d81259");
			put("outdoors_recreation", "4d4b7105d754a06377d81259");
			put("food", "4d4b7105d754a06374d81259");
			put("industrial_estate", "56aa371be4b08b9a8d5734d7");
			put("prison", "5310b8e5bcbc57f1066bcbf1");
			put("waste_facility", "58daa1558bbb0b01f18ec1ac");
		}};
	}

	public static int getNumberOfVenuesInRadius(String coordinates, int radiusMeters, VenueCategory venueCategory) throws IOException, URISyntaxException {
		int count = 0;
		
		try(
				CloseableHttpClient httpclient = HttpClients.createDefault();
		) {
			URIBuilder builder = new URIBuilder();
			builder = builder.setScheme("https").setHost("api.foursquare.com").setPath("/v2/venues/search");
			builder = builder.setParameter("client_id", getProperty("client_id"));
			builder = builder.setParameter("client_secret", getProperty("client_secret"));
			builder = builder.setParameter("v", "20171202");
			builder = builder.setParameter("intent", "browse");
			builder = builder.setParameter("limit", "50");
			builder = builder.setParameter("ll", coordinates);
			builder = builder.setParameter("radius", String.valueOf(radiusMeters));
			builder = builder.setParameter("categoryId", venueCategory.getId());


			HttpGet httpGet = new HttpGet(builder.build());
			try(
					CloseableHttpResponse response = httpclient.execute(httpGet);
			) {
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					try {
						String json = EntityUtils.toString(entity);
						System.out.println(json);
						Yeah yeah = JsonUtil.parseJson(json, Yeah.class);
						System.out.println(yeah.toPrettyJson());

						if(yeah.response != null) {
							for(Venue venue: yeah.response.venues) {
								if(venue.location != null && venue.location.distance != null) {
									if(venue.location.distance < radiusMeters) {
										count++;
									}
								}
							}
						}
					}
					finally {
						// and ensure it is fully consumed
						EntityUtils.consume(entity);
					}
				}
				else {
					System.out.println(response.getStatusLine());
				}
			}
		}

		return count;
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		System.out.printf("count: %d\n", getNumberOfVenuesInRadius("52.516667,13.388889", 1000, VenueCategory.FOOD));
	}

	public static String getCategoryId(String key) {
		return venueCategories.get(key);
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
