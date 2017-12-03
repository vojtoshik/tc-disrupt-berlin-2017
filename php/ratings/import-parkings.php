<?php

$f = fopen("parkings.csv", "r");
$counter = 0;
$batchCounter = 1;
$content = '';

while ($record = fgetcsv($f)) {
    $exportedData = [
        "id" => strtolower($record[0]),
        "location" => [
            "lat" => $record[14],
            "lon" => $record[13]
        ],
    ];

    $forElastic = [
        "index" => [
            "_index" => "parkings",
            "_type" => "spot",
            "_id" => $exportedData["id"]
        ]
    ];

    $content .= json_encode($forElastic) . "\n";
    $content .= json_encode($exportedData) . "\n";
    $counter++;

    if ($counter > 2000) {
        $counter = 0;
        file_put_contents("to_send.txt", $content);
        passthru('curl -X POST https://hack.cmlteam.com/_bulk --data-binary "@to_send.txt"');
        echo "Batch $batchCounter done...\n";
        $batchCounter++;
    }
}

file_put_contents("to_send.txt", $content);
passthru('curl -X POST https://hack.cmlteam.com/_bulk --data-binary "@to_send.txt"');
