<?php
// getRates.php

$CACHE_DIR = __DIR__ . '/cache';

$CACHE_FILE = $CACHE_DIR . '/rates.json';

if (!file_exists($CACHE_FILE)) {
    http_response_code(503);
    echo json_encode(["error" => "Exchange rates not available."]);
    exit;
}

header('Content-Type: application/json');
echo file_get_contents($CACHE_FILE);
