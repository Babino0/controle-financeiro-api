package br.com.Babino0.controle_financeiro_api.dto;

import br.com.Babino0.controle_financeiro_api.model.TipoTransacao;

import java.math.BigDecimal;

public record TransacaoRequest(Long contaId, Long categoriaId, TipoTransacao tipo,
                               BigDecimal valor, String descricao) {
}
