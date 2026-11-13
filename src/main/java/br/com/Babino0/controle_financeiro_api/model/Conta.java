package br.com.Babino0.controle_financeiro_api.model;

import br.com.Babino0.controle_financeiro_api.exception.SaldoInsuficienteException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
@Entity
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private  String nome;
    private BigDecimal saldo;

    private void depositar(BigDecimal valor){
         this.saldo = this.saldo.add(valor);

    }
    private void retirar(BigDecimal valor) {
        if (saldo.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException();
        }
        saldo = saldo.subtract(valor);
    }

    public void aplicar(TipoTransacao tipoTransacao, BigDecimal valor) {
        if(valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");

        }
        switch (tipoTransacao) {
            case RECEITA ->  this.depositar(valor);
            case DESPESA ->   this.retirar(valor);
        }
    }


    public Conta() {
    }

    public Conta(String nome, BigDecimal saldo) {
        this.nome = nome;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
