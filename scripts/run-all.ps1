$ok = 0
$failed = 0

foreach ($file in Get-ChildItem "$PSScriptRoot\*-test.ps1") {
    Write-Host "== $($file.Name)" -ForegroundColor Cyan
    $lines = & powershell -NoProfile -ExecutionPolicy Bypass -File $file.FullName
    foreach ($line in $lines) {
        if ($line -like "OK*") { Write-Host $line -ForegroundColor Green; $ok++ }
        elseif ($line -like "FAILED*") { Write-Host $line -ForegroundColor Red; $failed++ }
        else { Write-Host $line }
    }
}

Write-Host ""
Write-Host "Total: $ok OK, $failed FAILED" -ForegroundColor Cyan