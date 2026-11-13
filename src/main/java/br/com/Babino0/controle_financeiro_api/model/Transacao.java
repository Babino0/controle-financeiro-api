
package br.com.Babino0.controle_financeiro_api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private BigDecimal valor;

    private String descricao;

    private LocalDateTime dataHora = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "conta_id")
    private Conta conta;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    public Transacao() {
    }

    public Transacao(TipoTransacao tipo, BigDecimal valor,
                     String descricao, LocalDateTime dataHora,
                     Conta conta, Categoria categoria) {
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.dataHora = dataHora;
        this.conta = conta;
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Conta getConta() {
        return conta;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public Long getId() {
        return id;
    }
}
