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

function Balance() {
    $a = Call "GET" "/accounts/$accountId" $null
    return [decimal]($a.Body | ConvertFrom-Json).balance
}

function Send($accId, $catId, $type, $amount) {
    $json = '{"accountId":' + $accId + ',"categoryId":' + $catId + ',"type":"' + $type + '","amount":' + $amount + ',"description":"test"}'
    return Call "POST" "/transactions" $json
}

$cat = Call "POST" "/categories" '{"name":"Food"}'
$categoryId = ($cat.Body | ConvertFrom-Json).id
$acc = Call "POST" "/accounts" '{"name":"Wallet","balance":100}'
$accountId = ($acc.Body | ConvertFrom-Json).id

$r = Send $accountId $categoryId "EXPENSE" 30
Check "expense 30: status" $r.Status 201
Check "expense 30: balance" (Balance) 70

$r = Send $accountId $categoryId "INCOME" 50
Check "income 50: status" $r.Status 201
Check "income 50: balance" (Balance) 120

$r = Send $accountId $categoryId "EXPENSE" 120
Check "expense that zeroes the balance: status" $r.Status 201
Check "expense that zeroes the balance: balance" (Balance) 0

$r = Send $accountId $categoryId "EXPENSE" 1
Check "insufficient balance: status" $r.Status 422
Check "insufficient balance: balance unchanged" (Balance) 0

$r = Send $accountId $categoryId "INCOME" 0
Check "zero amount: status" $r.Status 400

$r = Send $accountId $categoryId "EXPENSE" -5
Check "negative amount: status" $r.Status 400
Check "invalid amount: balance unchanged" (Balance) 0

$r = Send 999 $categoryId "INCOME" 10
Check "unknown account: status" $r.Status 404

$r = Send $accountId 999 "INCOME" 10
Check "unknown category: status" $r.Status 404
Check "unknown resource: balance unchanged" (Balance) 0