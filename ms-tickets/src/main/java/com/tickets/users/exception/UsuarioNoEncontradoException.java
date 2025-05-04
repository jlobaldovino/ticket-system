package com.tickets.users.exception;

public class UsuarioNoEncontradoException extends BusinessException {
    public UsuarioNoEncontradoException(String email) {
        super("No se encontró el usuario con email: " + email);
    }
    public UsuarioNoEncontradoException(Object idOrEmail) {
        super("No se encontró el usuario con: " + idOrEmail);
    }
}