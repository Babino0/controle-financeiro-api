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

$acc = (Send "POST" "/accounts" '{"name":"Validation","balance":100}').Body | ConvertFrom-Json
$cat = (Send "POST" "/categories" '{"name":"Validation"}').Body | ConvertFrom-Json
$a = $acc.id
$c = $cat.id

$r = Send "POST" "/transactions" ('{"accountId":' + $a + ',"categoryId":' + $c + ',"type":"EXPENSE","amount":10,"description":"ok"}')
Check "1 valid transaction returns 201" ($r.Status -eq 201) "got $($r.Status)"

$r = Send "POST" "/transactions" ('{"accountId":' + $a + ',"categoryId":' + $c + ',"type":"EXPENSE"}')
Check "2 missing amount returns 400 with error key" ($r.Status -eq 400 -and $r.Body -match '"error"') "got $($r.Status) $($r.Body)"

$r = Send "POST" "/transactions" ('{"accountId":' + $a + ',"categoryId":' + $c + ',"amount":10}')
Check "3 missing type returns 400" ($r.Status -eq 400) "got $($r.Status)"

$r = Send "POST" "/transactions" ('{"categoryId":' + $c + ',"type":"EXPENSE","amount":10}')
Check "4 missing accountId returns 400" ($r.Status -eq 400) "got $($r.Status)"

$r = Send "POST" "/transactions" ('{"accountId":' + $a + ',"categoryId":' + $c + ',"type":"EXPENSE","amount":0}')
Check "5 amount 0 returns 400" ($r.Status -eq 400) "got $($r.Status)"

$long = "x" * 300
$r = Send "POST" "/transactions" ('{"accountId":' + $a + ',"categoryId":' + $c + ',"type":"EXPENSE","amount":10,"description":"' + $long + '"}')
Check "6 description with 300 chars returns 400" ($r.Status -eq 400) "got $($r.Status)"

$r = Send "POST" "/transactions" ('{"accountId":' + $a + ',"categoryId":' + $c + ',"type":"BANANA","amount":10}')
Check "7 invalid type returns 400" ($r.Status -eq 400) "got $($r.Status)"

Write-Host ""
Write-Host "Total: $ok OK, $fail FAILED"