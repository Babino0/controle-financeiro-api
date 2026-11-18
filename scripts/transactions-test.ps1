$base = "http://localhost:8080"

function Call($method, $path, $body) {
    $params = @{ Uri = "$base$path"; Method = $method; UseBasicParsing = $true }
    if ($body) {
        $params.ContentType = "application/json"
        $params.Body = $body
    }
    try {
        $r = Invoke-WebRequest @params
        return @{ Status = [int]$r.StatusCode; Body = $r.Content }
    } catch {
        return @{ Status = [int]$_.Exception.Response.StatusCode; Body = "" }
    }
}

function Check($name, $actual, $expected) {
    if ($actual -eq $expected) { Write-Host "OK: $name" -ForegroundColor Green }
    else { Write-Host "FAILED: $name (expected $expected, got $actual)" -ForegroundColor Red }
}

function Send($accId, $catId, $type, $amount, $desc) {
    $json = '{"accountId":' + $accId + ',"categoryId":' + $catId + ',"type":"' + $type + '","amount":' + $amount + ',"description":"' + $desc + '"}'
    return Call "POST" "/transactions" $json
}

function ListByAccount($accId) {
    return Call "GET" "/transactions?accountId=$accId" $null
}

function ParseList($body) {
    $parsed = $body | ConvertFrom-Json
    return @($parsed | ForEach-Object { $_ })
}

$categoryId = ((Call "POST" "/categories" '{"name":"Food"}').Body | ConvertFrom-Json).id
$acc1 = ((Call "POST" "/accounts" '{"name":"Wallet","balance":500}').Body | ConvertFrom-Json).id
$acc2 = ((Call "POST" "/accounts" '{"name":"Savings","balance":100}').Body | ConvertFrom-Json).id

Send $acc1 $categoryId "EXPENSE" 10 "first" | Out-Null
Start-Sleep -Milliseconds 50
Send $acc1 $categoryId "INCOME" 20 "second" | Out-Null
Start-Sleep -Milliseconds 50
Send $acc1 $categoryId "EXPENSE" 5 "third" | Out-Null
Send $acc2 $categoryId "INCOME" 1 "other" | Out-Null

$items = @(ParseList (ListByAccount $acc1).Body)
Check "list: count" $items.Count 3
Check "list: newest first" $items[0].description "third"
Check "list: oldest last" $items[2].description "first"
Check "isolation: account 1 still has 3" (@(ParseList (ListByAccount $acc1).Body)).Count 3
