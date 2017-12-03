package com.tc.hoodwatch.controllers;


import com.tc.hoodwatch.model.DataPoint;
import com.tc.hoodwatch.model.DataPoint1;
import com.tc.hoodwatch.model.GeoCell;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.geogrid.ParsedGeoHashGrid;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

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

    @ResponseBody
    @RequestMapping(value = "/berlin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<DataPoint1> getBerlin(
            @RequestParam(defaultValue = "10245") int postCode,
            @RequestParam(defaultValue = "3000") int n
    ) throws IOException {
        SearchRequest searchRequest = new SearchRequest("berlin");

        BoolQueryBuilder query = new BoolQueryBuilder();

        query.must(QueryBuilders.termQuery("postCode", postCode));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(n);

        log.info("Query: " + query);

        sourceBuilder.query(query);
        searchRequest.source(sourceBuilder);
        SearchResponse response = highLevelClient.search(searchRequest);

        SearchHits hits = response.getHits();

        List<DataPoint1> result = new ArrayList<>(hits.getHits().length);

        for (SearchHit hit : hits) {
            Map<String, Object> src = hit.getSourceAsMap();
//            System.out.println(src);
            Map<String,String> location = (Map) src.get("location");
            Map<String,Integer> values = (Map)src.get("values");

            result.add(new DataPoint1(
                    Double.parseDouble(location.get("lat")),
                    Double.parseDouble(location.get("lon")),
                    values.get("shops"),
                    values.get("transport"),
                    values.get("food"),
                    values.get("sport")
            ));
        }
        return result;
    }


    /**
     * Serve Open Street Map data
     *
     * @param category     set of categories
     *                     empty = any
     * @param n            limit
     * @param lat          latitude
     * @param lon          longitude
     * @param radiusMeters min radius in meters
     *                     0 = any
     * @return list of found data points
     */
    @ResponseBody
    @RequestMapping(value = "/osm", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<DataPoint> osm(
            String name,
            String category,
            @RequestParam(defaultValue = "0") int n,
            @RequestParam(defaultValue = "0") double lat,
            @RequestParam(defaultValue = "0") double lon,
            @RequestParam(defaultValue = "0") double radiusMeters) throws IOException {

        SearchRequest searchRequest = new SearchRequest("osm").types("osm");

        BoolQueryBuilder query = new BoolQueryBuilder();

        if (StringUtils.isNotEmpty(category)) {
            query.must(QueryBuilders.termsQuery("category", parseShorts(category)));
        }
        if (radiusMeters > 0) {
            query.must(QueryBuilders.geoDistanceQuery("location")
                    .distance(radiusMeters, DistanceUnit.METERS)
                    .point(lat, lon));
        }
        if (StringUtils.isNotEmpty(name)) {
            query.must(QueryBuilders.simpleQueryStringQuery(name).field("name"));
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
            Integer _category = (Integer) src.get("category");
            String _name = (String) src.get("name");
            String[] latlon = location.split(", ");

            result.add(new DataPoint(
                    _name,
                    Double.parseDouble(latlon[0]),
                    Double.parseDouble(latlon[1]),
                    _category.shortValue(),
                    0));
        }
        return result;
    }

    /**
     * Serve Open Street Map data
     *
     * @param precision precision
     * @param category  category
     *                  "" = any
     * @return cells
     */
    @ResponseBody
    @RequestMapping(value = "/osm_grid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<GeoCell> osmGrid(
            int precision,
            String category) throws IOException {

        SearchRequest searchRequest = new SearchRequest("osm").types("osm");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(0); // we use only aggregates
        AggregationBuilder gridAgg = AggregationBuilders.geohashGrid("cells")
                .field("location")
                .precision(precision);

        BoolQueryBuilder query = new BoolQueryBuilder();

        if (StringUtils.isNotEmpty(category)) {
            query.must(QueryBuilders.termsQuery("category", parseShorts(category)));
            sourceBuilder.query(query);
        }

//        if (StringUtils.isNotEmpty(category)) {
//            FilterAggregationBuilder aggregationBuilder = AggregationBuilders.filter("categories", QueryBuilders.termsQuery("category", parseShorts(category)));
//            gridAgg = aggregationBuilder.subAggregation(gridAgg);
//        }

        sourceBuilder.aggregation(gridAgg);

        System.out.println("Src:" + sourceBuilder);

        searchRequest.source(sourceBuilder);

        SearchResponse response = highLevelClient.search(searchRequest);

        Aggregations aggregations = response.getAggregations();

        List<GeoCell> cells = new ArrayList<>();

        for (Aggregation aggregation : aggregations) {
            if (aggregation instanceof ParsedGeoHashGrid) {
                ParsedGeoHashGrid grid = (ParsedGeoHashGrid) aggregation;
                for (GeoHashGrid.Bucket bucket : grid.getBuckets()) {
                    GeoPoint point = ((ParsedGeoHashGrid.ParsedBucket) bucket).getKey();
                    cells.add(new GeoCell(point.geohash(), point.lat(), point.lon(), (int) bucket.getDocCount()));
                }
            }
        }

        return cells;
    }

    /**
     * "1,2,3" -> [1,2,3]
     */
    private Set<Short> parseShorts(String str) {
        String[] parts = str.split(",");
        Set<Short> res = new HashSet<>();
        for (String part : parts) {
            res.add(Short.parseShort(part));
        }
        return res;
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
/*
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
                    "", Double.parseDouble(latlon[0]),
                    Double.parseDouble(latlon[1]),
                    _category,
                    score));
        }
        return result;
    }
*/
}
