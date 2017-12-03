<?php

include "vendor/autoload.php";

$client = new GuzzleHttp\Client();

//$housesCurrentPage = 0;
//$batchSize = 100;
//$totalRecords = 375340;

$parkingsSearchScript = json_decode(file_get_contents('requests/search-parkings.json'));
$searchCatRange = file_get_contents('requests/search-category-range.json');
$searchCat = file_get_contents('requests/search-category.json');
$updateScript = file_get_contents('requests/update-house.json');

$POSTS = [
    '14167',
    '14169',
    '14193',
    '14195',
    '14197',
    '14199',
    '10115',
    '10117',
    '10119',
    '10178',
    '10179',
    '10315',
    '10317',
    '10318',
    '10319',
    '10365',
    '10367',
    '10369',
    '10405',
    '10407',
    '10409',
    '10435',
    '10437',
    '10439',
    '10551',
    '10553',
    '10555',
    '10557',
    '10559',
    '10585',
    '10587',
    '10589',
    '10623',
    '10625',
    '10627',
    '10629',
    '10707',
    '10709',
    '10711',
    '10713',
    '10715',
    '10717',
    '10719',
    '10777',
    '10779',
    '10781',
    '10783',
    '10785',
    '10787',
    '10789',
    '10823',
    '10825',
    '10827',
    '10829',
    '11011',
    '12043',
    '12045',
    '12047',
    '12049',
    '12051',
    '12053',
    '12055',
    '12057',
    '12059',
    '12099',
    '12101',
    '12103',
    '12105',
    '12107',
    '12109',
    '12157',
    '12159',
    '12161',
    '12163',
    '12165',
    '12167',
    '12169',
    '12203',
    '12205',
    '12207',
    '12209',
    '12247',
    '12249',
    '12277',
    '12279',
    '12305',
    '12307',
    '12309',
    '12347',
    '12349',
    '12351',
    '12353',
    '12355',
    '12357',
    '12359',
    '12435',
    '12437',
    '12439',
    '12459',
    '12487',
    '12489',
    '12524',
    '12526',
    '12527',
    '12529',
    '12555',
    '12557',
    '12559',
    '12587',
    '12589',
    '12619',
    '12621',
    '12623',
    '12627',
    '12629',
    '12679',
    '12681',
    '12683',
    '12685',
    '12687',
    '12689',
    '13051',
    '13053',
    '13055',
    '13057',
    '13059',
    '13086',
    '13088',
    '13089',
    '13125',
    '13127',
    '13129',
    '13156',
    '13158',
    '13159',
    '13187',
    '13189',
    '13347',
    '13349',
    '13351',
    '13353',
    '13355',
    '13357',
    '13359',
    '13403',
    '13405',
    '13407',
    '13409',
    '13435',
    '13437',
    '13439',
    '13465',
    '13467',
    '13469',
    '13503',
    '13505',
    '13507',
    '13509',
    '13581',
    '13583',
    '13585',
    '13587',
    '13589',
    '13591',
    '13593',
    '13595',
    '13597',
    '13599',
    '13627',
    '13629',
    '14050',
    '14052',
    '14053',
    '14055',
    '14057',
    '14059',
    '14089',
    '14109',
    '14129',
    '14131',
    '14163',
    '14165',
    '10243',
    '10245',
    '10247',
    '10249',
    '10961',
    '10963',
    '10965',
    '10967',
    '10969',
    '10997',
    '10999',
    '15537',
    '13047'];

$j = 0;
$cnt = count($POSTS);
foreach ($POSTS as $p) {
    $j++;
    $searchRequest = [
        'from' => 0,
        'size' => 10000,
        'query' => [
            'term' => [
                'postCode' => $p
            ]
        ]
    ];

    $res = $client->request('GET', 'https://hack.cmlteam.com/berlin/_search', [
        'json' => $searchRequest
    ]);
    $hits = json_decode($res->getBody())->hits;

//        echo "Batch number: " . ($housesCurrentPage + 1) . "\n";

    $houses = $hits->hits;
    echo "========== post=$p ($j / $cnt), houses=" . count($houses) . "\n";
    $counter = 0;

    foreach ($houses as $item) {

        $location = $item->_source->location;
        $id = $item->_source->id;

        $shopsScore = getValueForCategory(105, $location);

        $parkingsSearchScript->query->bool->filter->geo_distance->location = $location;

        $parkingsNearby = json_decode($client->request('GET', 'https://hack.cmlteam.com/parkings/_search', ['json' => $parkingsSearchScript])->getBody());
//        echo "parc " . json_encode($parkingsNearby) . "\n";
        $parkingsScore = $parkingsNearby->hits->total;

        $sportScore = getValue($location, 110, 136);
        $transport = getValue($location, 159, 168);
        $food = getValueForCategory(25, $location);

        echo "$id --> $shopsScore, $sportScore,$transport, $food, $parkingsScore \n";
        $u = str_replace('$SHOPS$', $shopsScore, $updateScript);
        $u = str_replace('$SPORT$', $sportScore, $u);
        $u = str_replace('$TRANS$', $transport, $u);
        $u = str_replace('$FOOD$', $food, $u);
        $u = str_replace('$PARKING$', $parkingsScore, $u);

        $client->request('POST', 'https://hack.cmlteam.com/berlin/houses/' . $id . '/_update', ['json' => json_decode($u)]);

        $counter++;

        printf("Updated id:%d (#%d)\n", $id, $counter);
    }

    $housesCurrentPage++;
}

function getValue($location, $gte, $lte)
{
//    echo "loc $location->lat $location->lon\n";
    $client = new GuzzleHttp\Client();

    global $searchCatRange;
    $scr = str_replace('$LTE$', $lte, $searchCatRange);
    $scr = str_replace('$GTE$', $gte, $scr);
    $scr = json_decode($scr);
    $scr->query->bool->filter[0]->geo_distance->location = $location;

//    echo json_encode($scr);
    $itemsNearby = json_decode($client->request('GET', 'https://hack.cmlteam.com/osm/_search', ['json' => $scr])->getBody());
    return $itemsNearby->hits->total;
}

function getValueForCategory($category, $location)
{
//    echo "loc1 $location->lat $location->lon\n";
    $client = new GuzzleHttp\Client();

    global $searchCat;
    $scr = json_decode(str_replace('$CATEGORY$', $category, $searchCat));
    $scr->query->bool->filter[0]->geo_distance->location = $location;

//    echo json_encode($scr);
    $supermarketsNearby = json_decode($client->request('GET', 'https://hack.cmlteam.com/osm/_search', ['json' => $scr])->getBody());
    return $supermarketsNearby->hits->total;
}