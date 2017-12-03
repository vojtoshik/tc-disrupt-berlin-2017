<?php

include "vendor/autoload.php";

$client = new GuzzleHttp\Client();

$housesCurrentPage = 0;
$batchSize = 2;
$totalRecords = 375340;


while ($housesCurrentPage * $batchSize < $totalRecords) {

    $searchRequest = [
        'from' => $housesCurrentPage * $batchSize,
        'size' => $batchSize
    ];


    $res = $client->request('GET', 'https://hack.cmlteam.com/berlin/_search', [
        'json' => $searchRequest
    ]);
    $hits = json_decode($res->getBody())->hits;

    echo "Batch number: " . ($housesCurrentPage + 1) . "\n";

    $houses = $hits->hits;
    $counter = 0;

    foreach ($houses as $item) {

        $location = $item->_source->location;
        $id = $item->_source->id;

        $shopsScore = getValueForCategory(105, $location);

        $parkingsSearchScript = json_decode(file_get_contents('requests/search-parkings.json'));
        $parkingsSearchScript->query->bool->filter->geo_distance->location = $location;

        $parkingsNearby = json_decode($client->request('GET', 'https://hack.cmlteam.com/parkings/_search', ['json' => $parkingsSearchScript])->getBody());
        $parkingsScore = $parkingsNearby->hits->total;

        $sportScore = getValue($location, 110, 136);
        $transport = getValue($location, 159, 168);
        $food = getValueForCategory(25, $location);

        $updateScript = file_get_contents('requests/update-house.json');
        $updateScript = str_replace('$SHOPS$', $shopsScore, $updateScript);
        $updateScript = str_replace('$SPORT$', $sportScore, $updateScript);
        $updateScript = str_replace('$TRANS$', $transport, $updateScript);
        $updateScript = str_replace('$FOOD$', $food, $updateScript);
        $updateScript = str_replace('$PARKING$', $parkingsScore, $updateScript);

        $client->request('POST', 'https://hack.cmlteam.com/berlin/houses/' . $id . '/_update', ['json' => json_decode($updateScript)]);

        $counter++;

        printf("Updated id:%d (#%d)\n", $id, $counter);
    }

    $housesCurrentPage++;
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

function getValueForCategory($category, $location) {
    $client = new GuzzleHttp\Client();

    $pointsSearchScript = file_get_contents('requests/search-category.json');
    $pointsSearchScript = json_decode(str_replace('$CATEGORY$', $category, $pointsSearchScript));
    $pointsSearchScript->query->bool->filter[0]->geo_distance->location = $location;

    $supermarketsNearby = json_decode($client->request('GET', 'https://hack.cmlteam.com/osm/_search', ['json' => $pointsSearchScript])->getBody());
    return $supermarketsNearby->hits->total;
}