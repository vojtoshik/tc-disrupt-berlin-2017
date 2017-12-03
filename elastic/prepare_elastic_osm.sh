#!/usr/bin/env bash

#ES=http://es.mydev.name
ES=https://hack.cmlteam.com

curl -X DELETE "$ES/osm"
echo
echo

curl -X PUT "$ES/osm"
echo
echo

curl -X PUT "$ES/_template/default" -d '{
    "template": "*",
    "mappings":
    {
        "_default_":
        {
            "_all":
            {
                "enabled": false
            },

            "dynamic_templates":
            [
                {
                    "strings":
                    {
                        "match_mapping_type": "string",
                        "mapping":
                        {
                            "type": "string",
                            "analyzer": "snowball"
                        }
                    }
                }
            ]
        }
    }
}'
echo
echo

curl -X PUT "$ES/osm/osm/_mapping" -d '{
    "_all":
    {
        "enabled": false
    },
   "properties" : {
       "location" : { "type" : "geo_point"},
       "category" : { "type" : "short" },
       "name" : { "type" : "string" }
   }
}'
echo
echo

