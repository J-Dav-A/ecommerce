package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import java.time.LocalDate;
import java.util.Objects;

public class Usuario {

    private final String id;
    private final String nombre;
    private final String email;
    private final String contrasena;
    private final String telefono;
    private final String rol;
    private final LocalDate fechaNacimiento;

    private Usuario(String id, String nombre, String email, String contrasena, String telefono, String rol, LocalDate fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.rol = rol;
        this.fechaNacimiento = fechaNacimiento;
    }

    public static Usuario crear(String id, String nombre, String email, String contrasena, String telefono, String rol, LocalDate fechaNacimiento) {
        if (nombre == null || nombre.isBlank()) throw new ReglaDominioException("El nombre es obligatorio");
        if (email == null || email.isBlank()) throw new ReglaDominioException("El email es obligatorio");
        return new Usuario(id, nombre, email, contrasena, telefono, rol, fechaNacimiento);
    }

    public static Usuario reconstruir(String id, String nombre, String email, String contrasena, String telefono, String rol, LocalDate fechaNacimiento) {
        return new Usuario(id, nombre, email, contrasena, telefono, rol, fechaNacimiento);
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getContrasena() { return contrasena; }
    public String getTelefono() { return telefono; }
    public String getRol() { return rol; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }

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