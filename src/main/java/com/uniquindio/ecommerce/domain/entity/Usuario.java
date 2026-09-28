package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Usuario {

    private final UUID id;
    private final String nombre;
    private final String email;
    private final String contraseña;
    private final String telefono;
    private final String rol;
    private final LocalDate fechaNacimiento;

    private Usuario(
            UUID id,
            String nombre,
            String email,
            String contraseña,
            String telefono,
            String rol,
            LocalDate fechaNacimiento
    ) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
        this.telefono = telefono;
        this.rol = rol;
        this.fechaNacimiento = fechaNacimiento;
    }

    public static Usuario crear(
            UUID id,
            String nombre,
            String email,
            String contraseña,
            String telefono,
            String rol,
            LocalDate fechaNacimiento
    ) {

        if (id == null) {
            throw new ReglaDominioException(
                    "El usuario debe tener un identificador."
            );
        }

        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException(
                    "El nombre del usuario es obligatorio."
            );
        }

        if (email == null || email.isBlank()) {
            throw new ReglaDominioException(
                    "El email del usuario es obligatorio."
            );
        }

        if (contraseña == null || contraseña.isBlank()) {
            throw new ReglaDominioException(
                    "La contraseña del usuario es obligatoria."
            );
        }

        if (telefono == null || telefono.isBlank()) {
            throw new ReglaDominioException(
                    "El teléfono del usuario es obligatorio."
            );
        }

        if (rol == null || rol.isBlank()) {
            throw new ReglaDominioException(
                    "El rol del usuario es obligatorio."
            );
        }

        if (fechaNacimiento == null) {
            throw new ReglaDominioException(
                    "La fecha de nacimiento es obligatoria."
            );
        }

        return new Usuario(
                id,
                nombre,
                email,
                contraseña,
                telefono,
                rol,
                fechaNacimiento
        );
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getRol() {
        return rol;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Usuario otro)) return false;

        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}