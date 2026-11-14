package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import java.time.LocalDate;
import java.util.Objects;

public class Usuario {

    private final String id;
    private String nombre;
    private final String email;
    private String contrasena;
    private String telefono;
    private final String rol;
    private LocalDate fechaNacimiento;
    private boolean activo;

    private Usuario(String id, String nombre, String email, String contrasena, String telefono, String rol,
                    LocalDate fechaNacimiento, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.rol = rol;
        this.fechaNacimiento = fechaNacimiento;
        this.activo = activo;
    }

    public static Usuario crear(String id, String nombre, String email, String contrasena, String telefono, String rol, LocalDate fechaNacimiento) {
        if (nombre == null || nombre.isBlank()) throw new ReglaDominioException("El nombre es obligatorio");
        if (email == null || email.isBlank()) throw new ReglaDominioException("El email es obligatorio");
        validarFechaNacimiento(fechaNacimiento);
        return new Usuario(id, nombre, email, contrasena, telefono, rol, fechaNacimiento, true);
    }

    public static Usuario reconstruir(String id, String nombre, String email, String contrasena, String telefono, String rol, LocalDate fechaNacimiento) {
        return new Usuario(id, nombre, email, contrasena, telefono, rol, fechaNacimiento, true);
    }

    public static Usuario reconstruir(String id, String nombre, String email, String contrasena, String telefono, String rol, LocalDate fechaNacimiento, boolean activo) {
        return new Usuario(id, nombre, email, contrasena, telefono, rol, fechaNacimiento, activo);
    }

    // El email y el rol no se editan: el email es la identidad de la cuenta y el rol define sus permisos
    public void editarPerfil(String nombre, String telefono, LocalDate fechaNacimiento) {
        if (nombre == null || nombre.isBlank()) throw new ReglaDominioException("El nombre es obligatorio");
        validarFechaNacimiento(fechaNacimiento);
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Recibe la contraseña ya cifrada
    public void cambiarContrasena(String contrasenaCifrada) {
        if (contrasenaCifrada == null || contrasenaCifrada.isBlank()) {
            throw new ReglaDominioException("La contraseña es obligatoria");
        }
        this.contrasena = contrasenaCifrada;
    }

    // Borrado lógico: la cuenta queda guardada pero ya no puede iniciar sesión
    public void desactivar() {
        if (!activo) throw new ReglaDominioException("La cuenta ya se encuentra eliminada");
        this.activo = false;
    }

    private static void validarFechaNacimiento(LocalDate fecha) {
        if (fecha != null && fecha.isAfter(LocalDate.now())) {
            throw new ReglaDominioException("La fecha de nacimiento no puede ser futura");
        }
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getContrasena() { return contrasena; }
    public String getTelefono() { return telefono; }
    public String getRol() { return rol; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public boolean estaActivo() { return activo; }

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