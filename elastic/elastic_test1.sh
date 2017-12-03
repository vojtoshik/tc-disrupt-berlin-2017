#!/usr/bin/env bash

#ES=http://es.mydev.name
ES=https://hack.cmlteam.com

curl -s -X GET "$ES/parkings/_search?pretty" -d'
{
"sort": [
        { "_uid":   { "order": "asc" }}
        ],
  "query":{
    "bool":{
      "must":
      {
        "match_all":{}

      },
      "filter": {
        "geo_distance":{
          "distance":"300m",
          "location":{
            "lat":52.507793,
            "lon":13.467648
          }
        }
      }
    }
  }
}'

exit

curl -s -X GET "$ES/berlin/_search?pretty" -d'
{
    "from" : 0,
    "size" : 3000,
    "query" : {
        "term" : { "postCode" : 10245 }
    }
}'

exit

curl -s -X POST "$ES/osm/_search?size=0&pretty" -d'{
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