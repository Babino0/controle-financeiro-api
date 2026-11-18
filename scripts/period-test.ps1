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

function ParseList($body) {
    if ([string]::IsNullOrWhiteSpace($body)) { return @() }
    $parsed = $body | ConvertFrom-Json
    return @($parsed | ForEach-Object { $_ })
}

function ListPeriod($accId, $query) {
    return Call "GET" "/transactions?accountId=$accId$query" $null
}

$today = (Get-Date).ToString("yyyy-MM-dd")
$yesterday = (Get-Date).AddDays(-1).ToString("yyyy-MM-dd")
$tomorrow = (Get-Date).AddDays(1).ToString("yyyy-MM-dd")

$categoryId = ((Call "POST" "/categories" '{"name":"Food"}').Body | ConvertFrom-Json).id
$acc = ((Call "POST" "/accounts" '{"name":"Wallet","balance":500}').Body | ConvertFrom-Json).id

Send $acc $categoryId "EXPENSE" 10 "first" | Out-Null
Send $acc $categoryId "INCOME" 20 "second" | Out-Null
Send $acc $categoryId "EXPENSE" 5 "third" | Out-Null

$r = ListPeriod $acc "&from=$today&to=$today"
Check "today..today: status" $r.Status 200
Check "today..today: count" (@(ParseList $r.Body)).Count 3
Check "yesterday..yesterday: count" (@(ParseList (ListPeriod $acc "&from=$yesterday&to=$yesterday").Body)).Count 0
Check "tomorrow..tomorrow: count" (@(ParseList (ListPeriod $acc "&from=$tomorrow&to=$tomorrow").Body)).Count 0
Check "yesterday..tomorrow: count" (@(ParseList (ListPeriod $acc "&from=$yesterday&to=$tomorrow").Body)).Count 3
Check "no dates: count (as before)" (@(ParseList (ListPeriod $acc "").Body)).Count 3
Check "only from: status" (ListPeriod $acc "&from=$today").Status 400
Check "from after to: status" (ListPeriod $acc "&from=$tomorrow&to=$yesterday").Status 400
Check "invalid date: status" (ListPeriod $acc "&from=abc&to=$today").Status 400
Check "unknown account with dates: status" (ListPeriod 999 "&from=$today&to=$today").Status 404