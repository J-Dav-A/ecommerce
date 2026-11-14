package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    private String id;
    private String nombre;
    private String email;
    private String contrasena;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String rol;

    // Borrado lógico; el valor por defecto permite que el data.sql y los registros previos queden activos
    @ColumnDefault("true")
    @jakarta.persistence.Column(nullable = false)
    private boolean activo = true;

    public String getPassword() {
        return contrasena;
    }

    public void setPassword(String password) {
        this.contrasena = password;
    }
}