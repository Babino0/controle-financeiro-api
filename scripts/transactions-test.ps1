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

function Send($accId, $catId, $type, $amount) {
    $json = '{"accountId":' + $accId + ',"categoryId":' + $catId + ',"type":"' + $type + '","amount":' + $amount + ',"description":"test"}'
    return Call "POST" "/transactions" $json
}

function ParseList($body) {
    if ([string]::IsNullOrWhiteSpace($body)) { return @() }
    $parsed = $body | ConvertFrom-Json
    return @($parsed | ForEach-Object { $_ })
}

function Report($accId) {
    return Call "GET" "/reports/by-category?accountId=$accId" $null
}

$food = ((Call "POST" "/categories" '{"name":"Food"}').Body | ConvertFrom-Json).id
$transport = ((Call "POST" "/categories" '{"name":"Transport"}').Body | ConvertFrom-Json).id
$acc1 = ((Call "POST" "/accounts" '{"name":"Wallet","balance":500}').Body | ConvertFrom-Json).id
$acc2 = ((Call "POST" "/accounts" '{"name":"Savings","balance":100}').Body | ConvertFrom-Json).id
$acc3 = ((Call "POST" "/accounts" '{"name":"Empty","balance":0}').Body | ConvertFrom-Json).id

Send $acc1 $food "EXPENSE" 10 | Out-Null
Send $acc1 $food "EXPENSE" 5 | Out-Null
Send $acc1 $transport "EXPENSE" 30 | Out-Null
Send $acc1 $food "INCOME" 100 | Out-Null
Send $acc2 $transport "EXPENSE" 7 | Out-Null

$r = Report $acc1
$items = @(ParseList $r.Body)
Check "report: status" $r.Status 200
Check "report: 2 categories" $items.Count 2
Check "report: biggest first" $items[0].categoryName "Transport"
Check "report: transport total" ([decimal]$items[0].total) 30
Check "report: food total ignores income" ([decimal]$items[1].total) 15

$r2 = @(ParseList (Report $acc2).Body)
Check "isolation: account 2 has 1 category" $r2.Count 1
Check "isolation: account 2 total" ([decimal]$r2[0].total) 7

$r3 = Report $acc3
Check "no expenses: status" $r3.Status 200
Check "no expenses: body is []" $r3.Body.Trim() "[]"
Check "unknown account: status" (Report 999).Status 404
Check "missing accountId: status" (Call "GET" "/reports/by-category" $null).Status 400