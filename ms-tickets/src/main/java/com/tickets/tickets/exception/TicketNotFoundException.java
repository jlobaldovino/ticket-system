package com.tickets.tickets.exception;

public class TicketNotFoundException extends BusinessException {
    public TicketNotFoundException(String id) {
        super("No se encontró el ticket con el ID: " + id);
    }
}