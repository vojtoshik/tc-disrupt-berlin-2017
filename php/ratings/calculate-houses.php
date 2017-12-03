<?php

include "vendor/autoload.php";

$client = new GuzzleHttp\Client();

$searchRequest = [
    'from' => 0,
    'size' => 3000,
    'query' => [
        'term' => [
            'postCode' => 10245
        ]
    ]
];


$res = $client->request('GET', 'https://hack.cmlteam.com/berlin/_search', [
    'json' => $searchRequest
]);
$hits = json_decode($res->getBody())->hits;

echo "Total houses: " . $hits->total . "\n";

$houses = $hits->hits;

foreach ($houses as $item) {
    $location = $item->_source->location;
    $id = $item->_source->id;

    $pointsSearchScript = json_decode(file_get_contents('requests/search-supermarkets-nearby.json'));
    $pointsSearchScript->query->bool->filter[0]->geo_distance->location = $location;

    $shopsScore = 0;
    $supermarketsNearby = json_decode($client->request('GET', 'https://hack.cmlteam.com/osm/_search', ['json' => $pointsSearchScript])->getBody());
    $shopsScore = $supermarketsNearby->hits->total;

    $sportScore = getValue($location, 110, 136);
    $transport = getValue($location, 159, 168);
    $food = getValue($location, 23, 29);

    $updateScript = file_get_contents('requests/update-house.json');
    $updateScript = str_replace('$SHOPS$', $shopsScore, $updateScript);
    $updateScript = str_replace('$SPORT$', $sportScore, $updateScript);
    $updateScript = str_replace('$TRANS$', $transport, $updateScript);
    $updateScript = str_replace('$FOOD$', $food, $updateScript);

    echo 'https://hack.cmlteam.com/berlin/houses/' . $id . '/_update' . "\n";
    echo $updateScript . "\n\n";


    $client->request('POST', 'https://hack.cmlteam.com/berlin/houses/' . $id . '/_update', ['json' => json_decode($updateScript)]);
}

function getValue($location, $gte, $lte) {

    $client = new GuzzleHttp\Client();

    $pointsSearchScript = file_get_contents('requests/search-category-range.json');
    $pointsSearchScript = str_replace('$LTE$', $lte, $pointsSearchScript);
    $pointsSearchScript = str_replace('$GTE$', $gte, $pointsSearchScript);
    $pointsSearchScript = json_decode($pointsSearchScript);
    $pointsSearchScript->query->bool->filter[0]->geo_distance->location = $location;

    $itemsNearby = json_decode($client->request('GET', 'https://hack.cmlteam.com/osm/_search', ['json' => $pointsSearchScript])->getBody());
    return $itemsNearby->hits->total;
}
