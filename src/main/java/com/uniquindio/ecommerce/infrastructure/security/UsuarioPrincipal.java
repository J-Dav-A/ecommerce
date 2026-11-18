package com.uniquindio.ecommerce.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

// Usuario autenticado que, además del email y los roles, conoce su id.
// Así los controllers pueden tomar el id del token (vía @AuthenticationPrincipal)
// y no del cuerpo de la petición, donde cualquiera podría poner el de otra persona.
public class UsuarioPrincipal extends User {

    private final String id;

    public UsuarioPrincipal(String id, String email, String password,
                            Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}