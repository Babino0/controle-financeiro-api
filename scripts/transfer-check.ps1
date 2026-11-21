$base = "http://localhost:8080"
$ok = 0
$fail = 0

function Send($method, $path, $json) {
    try {
        if ($json) {
            $r = Invoke-WebRequest -UseBasicParsing -Method $method -Uri "$base$path" -ContentType "application/json" -Body $json
        } else {
            $r = Invoke-WebRequest -UseBasicParsing -Method $method -Uri "$base$path"
        }
        return @{ Status = [int]$r.StatusCode; Body = $r.Content }
    } catch {
        $resp = $_.Exception.Response
        if ($resp -eq $null) { return @{ Status = 0; Body = $_.Exception.Message } }
        $reader = New-Object System.IO.StreamReader($resp.GetResponseStream())
        return @{ Status = [int]$resp.StatusCode; Body = $reader.ReadToEnd() }
    }
}

function Check($name, $condition, $detail) {
    if ($condition) {
        Write-Host "OK     $name" -ForegroundColor Green
        $script:ok++
    } else {
        Write-Host "FAILED $name  ($detail)" -ForegroundColor Red
        $script:fail++
    }
}

function Transfer($from, $to, $amount) {
    $json = '{"fromAccountId":' + $from + ',"toAccountId":' + $to + ',"amount":' + $amount + '}'
    return Send "POST" "/transfers" $json
}

function Balance($id) {
    $r = Send "GET" "/accounts" $null
    $list = $r.Body | ConvertFrom-Json
    foreach ($acc in $list) {
        if ($acc.id -eq $id) { return [decimal]$acc.balance }
    }
    return $null
}

# Setup: A = 100, B = 50
$a = (Send "POST" "/accounts" '{"name":"Transfer A","balance":100}').Body | ConvertFrom-Json
$b = (Send "POST" "/accounts" '{"name":"Transfer B","balance":50}').Body | ConvertFrom-Json

# 1-2 valid transfer
$r = Transfer $a.id $b.id 30
Check "1 valid transfer returns 204" ($r.Status -eq 204) "got $($r.Status)"
Check "2 balances after: A=70, B=80" ((Balance $a.id) -eq 70 -and (Balance $b.id) -eq 80) "A=$(Balance $a.id) B=$(Balance $b.id)"

# 3-4 insufficient balance, nothing changes
$r = Transfer $a.id $b.id 500
Check "3 insufficient balance returns 422" ($r.Status -eq 422) "got $($r.Status)"
Check "4 balances unchanged after failure" ((Balance $a.id) -eq 70 -and (Balance $b.id) -eq 80) "A=$(Balance $a.id) B=$(Balance $b.id)"

# 5 same account
$r = Transfer $a.id $a.id 10
Check "5 same account returns 400" ($r.Status -eq 400) "got $($r.Status)"

# 6-7 account not found
$r = Transfer 999999 $b.id 10
Check "6 missing source returns 404" ($r.Status -eq 404) "got $($r.Status)"
$r = Transfer $a.id 999999 10
Check "7 missing destination returns 404" ($r.Status -eq 404) "got $($r.Status)"

# 8-9 invalid amount
$r = Transfer $a.id $b.id 0
Check "8 amount 0 returns 400" ($r.Status -eq 400) "got $($r.Status)"
$r = Transfer $a.id $b.id -5
Check "9 negative amount returns 400" ($r.Status -eq 400) "got $($r.Status)"

# 10-11 validation: missing fields (needs @Valid + your handler)
$r = Send "POST" "/transfers" ('{"fromAccountId":' + $a.id + ',"toAccountId":' + $b.id + '}')
Check "10 missing amount returns 400" ($r.Status -eq 400) "got $($r.Status)"
Check "11 missing amount body has error key" ($r.Body -match '"error"') "body: $($r.Body)"
$r = Send "POST" "/transfers" ('{"fromAccountId":' + $a.id + ',"amount":10}')
Check "12 missing toAccountId returns 400" ($r.Status -eq 400) "got $($r.Status)"

Write-Host ""
Write-Host "Total: $ok OK, $fail FAILED"