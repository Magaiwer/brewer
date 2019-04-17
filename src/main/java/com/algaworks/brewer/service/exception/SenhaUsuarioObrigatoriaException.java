package com.algaworks.brewer.service.exception;

public class SenhaUsuarioObrigatoriaException extends RuntimeException {
    public SenhaUsuarioObrigatoriaException(String message) {
        super(message);
    }
}
