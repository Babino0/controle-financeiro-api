package br.com.Babino0.controle_financeiro_api.controller;

import br.com.Babino0.controle_financeiro_api.dto.TransacaoRequest;
import br.com.Babino0.controle_financeiro_api.model.Transacao;
import br.com.Babino0.controle_financeiro_api.service.TransacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {
    private final TransacaoService transacaoService;


    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }
    @PostMapping
    public ResponseEntity<Transacao> cadastrarTransacao(@RequestBody TransacaoRequest request) {
        Transacao salva = transacaoService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }
}
