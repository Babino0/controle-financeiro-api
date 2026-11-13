$base = "http://localhost:8080"

function Chamar($metodo, $caminho, $corpo) {
    $params = @{ Uri = "$base$caminho"; Method = $metodo; UseBasicParsing = $true }
    if ($corpo) {
        $params.ContentType = "application/json"
        $params.Body = $corpo
    }
    try {
        $r = Invoke-WebRequest @params
        return @{ Status = [int]$r.StatusCode; Corpo = $r.Content }
    } catch {
        return @{ Status = [int]$_.Exception.Response.StatusCode; Corpo = "" }
    }
}

function Conferir($nome, $obtido, $esperado) {
    if ($obtido -eq $esperado) { Write-Host "OK: $nome" -ForegroundColor Green }
    else { Write-Host "FALHOU: $nome (esperado $esperado, veio $obtido)" -ForegroundColor Red }
}

function Saldo() {
    $c = Chamar "GET" "/contas/$contaId" $null
    return [decimal]($c.Corpo | ConvertFrom-Json).saldo
}

function Enviar($cId, $catId, $tipo, $valor) {
    $json = '{"contaId":' + $cId + ',"categoriaId":' + $catId + ',"tipo":"' + $tipo + '","valor":' + $valor + ',"descricao":"teste"}'
    return Chamar "POST" "/transacoes" $json
}

$cat = Chamar "POST" "/categorias" '{"nome":"Alimentacao"}'
$categoriaId = ($cat.Corpo | ConvertFrom-Json).id
$conta = Chamar "POST" "/contas" '{"nome":"Carteira","saldo":100}'
$contaId = ($conta.Corpo | ConvertFrom-Json).id

$r = Enviar $contaId $categoriaId "DESPESA" 30
Conferir "despesa 30: status" $r.Status 201
Conferir "despesa 30: saldo" (Saldo) 70

$r = Enviar $contaId $categoriaId "RECEITA" 50
Conferir "receita 50: status" $r.Status 201
Conferir "receita 50: saldo" (Saldo) 120

$r = Enviar $contaId $categoriaId "DESPESA" 120
Conferir "despesa que zera o saldo: status" $r.Status 201
Conferir "despesa que zera o saldo: saldo" (Saldo) 0

$r = Enviar $contaId $categoriaId "DESPESA" 1
Conferir "saldo insuficiente: status" $r.Status 422
Conferir "saldo insuficiente: saldo nao mudou" (Saldo) 0

$r = Enviar $contaId $categoriaId "RECEITA" 0
Conferir "valor zero: status" $r.Status 400

$r = Enviar $contaId $categoriaId "DESPESA" -5
Conferir "valor negativo: status" $r.Status 400
Conferir "valor invalido: saldo nao mudou" (Saldo) 0

$r = Enviar 999 $categoriaId "RECEITA" 10
Conferir "conta inexistente: status" $r.Status 404

$r = Enviar $contaId 999 "RECEITA" 10
Conferir "categoria inexistente: status" $r.Status 404
Conferir "recurso inexistente: saldo nao mudou" (Saldo) 0