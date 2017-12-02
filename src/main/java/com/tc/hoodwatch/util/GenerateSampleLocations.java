package com.tc.hoodwatch.util;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class GenerateSampleLocations {
    public static final String ES_HOST = "es.mydev.name";

    public static final Random random = new Random();

    public static void main(String[] args) throws IOException {

        RestClient restClient = RestClient.builder(new HttpHost(ES_HOST, 80)).build();

        RestHighLevelClient highLevelClient = new RestHighLevelClient(restClient);

        BulkRequest bulkRequest = new BulkRequest();

        for (int i=0; i<1000; i++) {
            HashMap<String,Object> source = new HashMap<>();

            source.put("category", "noise");
            source.put("score", random.nextDouble());
            source.put("location", randomLat() + ", " + randomLon());

            bulkRequest.add(new IndexRequest("sample", "sample")
            .source(source));
        }

        highLevelClient.bulk(bulkRequest);
    }

    /*
    * Latitude : max/min +90 to -90
     */
    private static double randomLat() {
        return 90 * (random.nextDouble() * 2 - 1);
    }

    /*
    * Longitude : max/min +180 to -180
     */
    private static double randomLon() {
        return 180 * (random.nextDouble() * 2 - 1);
    }
}
