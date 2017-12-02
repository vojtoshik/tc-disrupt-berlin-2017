package com.tc.hoodwatch.util;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class IngestBerlinInEs {
    public static final String ES_HOST = "es.mydev.name";
    public static final String FILE = "tmp/berlin_germany.csv";
    public static final String INDEX = "osm";
    public static final int BATCH = 1000;

    public static void main(String[] args) throws IOException {

        RestClient restClient = RestClient.builder(new HttpHost(ES_HOST, 80)).build();

        RestHighLevelClient highLevelClient = new RestHighLevelClient(restClient);

        BulkRequest bulkRequest = new BulkRequest();

        Scanner scanner = new Scanner(new File(FILE), "UTF-8");

        int i = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            String[] parts = line.split("\\|", 5);

            if (parts.length < 5) {
                System.out.println("Skipping: " + line);
                continue;
            }


            HashMap<String,Object> source = new HashMap<>();

            source.put("category", Short.parseShort(parts[0]));
            source.put("location", Double.parseDouble(parts[2]) + ", " + Double.parseDouble(parts[3]));
            source.put("name", parts[4]);

            bulkRequest.add(new IndexRequest(INDEX, INDEX)
                    .id(parts[1])
                    .source(source));
            i++;

            if (i % BATCH == 0) {
                System.out.println("Progress: " + i + "...");
                highLevelClient.bulk(bulkRequest);
                bulkRequest = new BulkRequest();
            }
        }

        highLevelClient.bulk(bulkRequest);
        restClient.close();
    }
}
