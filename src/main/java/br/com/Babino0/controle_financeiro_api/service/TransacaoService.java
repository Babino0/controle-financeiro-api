package br.com.Babino0.controle_financeiro_api.service;

import br.com.Babino0.controle_financeiro_api.dto.TransacaoRequest;
import br.com.Babino0.controle_financeiro_api.exception.RecursoNaoEncontradoException;
import br.com.Babino0.controle_financeiro_api.model.Categoria;
import br.com.Babino0.controle_financeiro_api.model.Conta;
import br.com.Babino0.controle_financeiro_api.model.Transacao;
import br.com.Babino0.controle_financeiro_api.repository.CategoriaRepository;
import br.com.Babino0.controle_financeiro_api.repository.ContaRepository;
import br.com.Babino0.controle_financeiro_api.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class TransacaoService {

    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;

    public TransacaoService(ContaRepository contaRepository,
                            TransacaoRepository transacaoRepository,
                            CategoriaRepository categoriaRepository) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Transacao registrar(TransacaoRequest r) {
        Conta conta = contaRepository.findById(r.contaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta nao encontrada"));

        Categoria categoria = categoriaRepository.findById(r.categoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria nao encontrada"));

        conta.aplicar(r.tipo(), r.valor());

        contaRepository.save(conta);

        Transacao transacao = new Transacao(r.tipo(), r.valor(), r.descricao(), LocalDateTime.now(), conta, categoria);
        return transacaoRepository.save(transacao);
    }
}
