package com.tickets.users.exception;

public class EmailYaRegistradoException extends BusinessException {
    public EmailYaRegistradoException(String email) {
        super("El correo electrónico '" + email + "' ya está registrado.");
    }
}

