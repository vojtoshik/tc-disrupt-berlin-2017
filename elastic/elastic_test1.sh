#!/usr/bin/env bash

ES=http://es.mydev.name

curl -X POST "$ES/osm/_search?size=0&pretty" -d'{
    "aggregations" : {
        "cells" : {
            "geohash_grid" : {
                "field" : "location",
                "precision" : 6
            },
            "aggs": {
                "center_lat": {
                    "avg": {
                        "script": "doc[\"location\"].lat"
                    }
                },
                "center_lon": {
                    "avg": {
                        "script": "doc[\"location\"].lon"
                    }
                }
            }
        }
    }
}'