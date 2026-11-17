package br.com.Babino0.controle_financeiro_api.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient balance");
    }
}
