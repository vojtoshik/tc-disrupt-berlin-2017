package com.tc.hoodwatch.controllers;


import com.tc.hoodwatch.model.DataPoint;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class RestMainController {
    private static final Logger log = LoggerFactory.getLogger(RestMainController.class);

    private final RestHighLevelClient highLevelClient;

    @Autowired
    public RestMainController(RestHighLevelClient highLevelClient) {
        this.highLevelClient = highLevelClient;
    }

    @RequestMapping(value = "/endpoint", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String endpoint() {
        return "Yo";
    }

    /**
     * @param category     category
     *                     "" = any
     * @param n            limit
     * @param minScore     min score
     *                     0 = all
     * @param radiusMeters min radius in meters
     *                     0 = any
     * @return list of found data points
     */
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<DataPoint> search(String category,
                                  @RequestParam(defaultValue = "0") int n,
                                  @RequestParam(defaultValue = "0") double minScore,
                                  @RequestParam(defaultValue = "0") double lat,
                                  @RequestParam(defaultValue = "0") double lon,
                                  @RequestParam(defaultValue = "0") double radiusMeters) throws IOException {
        SearchRequest searchRequest = new SearchRequest("sample").types("sample");

        BoolQueryBuilder query = new BoolQueryBuilder();

        if (StringUtils.isNotEmpty(category)) {
            query.must(QueryBuilders.termQuery("category", category));
        }
        if (minScore > 0) {
            query.must(QueryBuilders.rangeQuery("score").gt(minScore));
        }
        if (radiusMeters > 0) {
            query.must(QueryBuilders.geoDistanceQuery("location")
                    .distance(radiusMeters, DistanceUnit.METERS)
                    .point(lat, lon));
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (n > 0) {
            sourceBuilder.size(n);
        }

        log.info("Query: " + query);

        sourceBuilder.query(query);
        searchRequest.source(sourceBuilder);
        SearchResponse response = highLevelClient.search(searchRequest);

        SearchHits hits = response.getHits();

        List<DataPoint> result = new ArrayList<>(hits.getHits().length);

        for (SearchHit hit : hits) {
            Map<String, Object> src = hit.getSourceAsMap();
//            System.out.println(src);
            String location = (String) src.get("location");
            String _category = (String) src.get("category");
            Double score = (Double) src.get("score");
            String[] latlon = location.split(", ");

            result.add(new DataPoint(
                    Double.parseDouble(latlon[0]),
                    Double.parseDouble(latlon[1]),
                    _category,
                    score));
        }
        return result;
    }
}
