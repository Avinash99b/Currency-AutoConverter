<?php
$CACHE_DIR = __DIR__ . '/cache';
$CACHE_FILE = $CACHE_DIR . '/rates.json';
$TIMESTAMP_FILE = $CACHE_DIR . '/last_updated.txt';

$API_KEY = getenv('EXCHANGE_API_KEY');
if (!$API_KEY) {
    echo "❌ Missing API key.\n";
    exit;
}

$API_URL = "https://v6.exchangerate-api.com/v6/{$API_KEY}/latest/USD";

// Ensure cache directory exists
if (!file_exists($CACHE_DIR)) {
    mkdir($CACHE_DIR, 0755, true);
}

$lastUpdated = file_exists($TIMESTAMP_FILE) ? intval(file_get_contents($TIMESTAMP_FILE)) : 0;
$now = time();
$eightHours = 8 * 60 * 60;

if (($now - $lastUpdated) < $eightHours) {
    echo "⏳ Skipping update. Last updated " . round(($now - $lastUpdated) / 3600, 2) . " hours ago.\n";
    exit;
}

$response = file_get_contents($API_URL);
if (!$response) {
    echo "❌ Failed to fetch data from API.\n";
    exit;
}

$data = json_decode($response, true);
if (!isset($data['result']) || $data['result'] !== 'success') {
    echo "❌ API returned error or malformed data.\n";
    exit;
}

$rawRates = $data['conversion_rates'];
$roundedRates = [];

foreach ($rawRates as $currency => $rate) {
    $roundedRates[$currency] = number_format((float)$rate, 4, '.', '');
}

file_put_contents($CACHE_FILE, json_encode([
    "conversion_rates" => $roundedRates
], JSON_PRETTY_PRINT | JSON_PRESERVE_ZERO_FRACTION));

file_put_contents($TIMESTAMP_FILE, $now);
echo "✅ Rates updated successfully at " . date('Y-m-d H:i:s') . "\n";
