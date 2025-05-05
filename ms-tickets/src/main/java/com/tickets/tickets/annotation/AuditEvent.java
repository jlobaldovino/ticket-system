package com.tickets.tickets.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditEvent {
    String accion();
    String servicio();
}
