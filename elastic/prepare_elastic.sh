#!/usr/bin/env bash

ES=http://es.mydev.name

curl -X DELETE "$ES/sample"
echo

curl -X PUT "$ES/sample" -d '{
   "analysis":{
       "analyzer":{
           "flat" : {
               "type" : "custom",
               "tokenizer" : "keyword",
               "filter" : "lowercase"
           }
       }
   }
}'
echo

curl -X PUT "$ES/sample/sample/_mapping" -d '{
    "_all":
    {
        "enabled": false
    },
   "properties" : {
       "location" : { "type" : "geo_point"},
       "category" : { "type" : "string" },
       "score" : { "type" : "double" }
   }
}'
echo
