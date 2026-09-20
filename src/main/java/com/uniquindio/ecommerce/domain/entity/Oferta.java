package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;

import java.util.Objects;
import java.util.UUID;

public class Oferta {

    private final UUID id;
    private final UUID vendedorId;
    private final String titulo;
    private Plataforma plataforma;
    private Precio precio;
    private int stock;
    private EstadoOferta estado;

    public Oferta(UUID id, UUID vendedorId, String titulo, Precio precio, int stock) {
        this.id = id;
        this.vendedorId = vendedorId;
        this.titulo = titulo;
        this.precio = precio;
        this.stock = stock;
        this.estado = EstadoOferta.BORRADOR;
    }

    public void definirPlataforma(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    public void cambiarPrecio(Precio nuevoPrecio) {
        if (nuevoPrecio == null) {
            throw new ReglaDominioException("El nuevo precio no puede ser nulo");
        }
        this.precio = nuevoPrecio;
    }

    /** Regla 1 de invarianza de publicacion: Una oferta solo puede ser publicada si tiene un precio definido y un stock mayor a cero. */

    public void publicar() {
        if (plataforma == null) {
            throw new ReglaDominioException("No se puede publicar la oferta sin una plataforma definida");
        }
        if (estado == EstadoOferta.ELIMINADA) {
            throw new ReglaDominioException("No se puede publicar una oferta eliminada");
        }
        this.estado = EstadoOferta.PUBLICADA;
    }

    public void ocultar() {
        if (estado == EstadoOferta.ELIMINADA) {
            throw new ReglaDominioException("La oferta ya fue eliminada.");
        }
        this.estado = EstadoOferta.INACTIVA;
    }

    /** regla 6 el stock nunca puede ser negativo */

    public void descontarStock(int cantidad) {
        if (cantidad < 0) {
            throw new ReglaDominioException("La cantidad a descontar no puede ser negativa");
        }
        if (cantidad > stock) {
            throw new ReglaDominioException("No se puede descontar más stock del disponible");
        }
        this.stock -= cantidad;
    }

    /** regla 5 eliminacion logica no se borra la oferta de la base de datos, solo se cambia su estado a eliminada */

    public void eliminar() {
        if (estado == EstadoOferta.ELIMINADA) {
            throw new ReglaDominioException("La oferta ya fue eliminada.");
        }
        this.estado = EstadoOferta.ELIMINADA;
    }

    public UUID getId() {return id;}

    public UUID getVendedorId() {return vendedorId;}

    public String getTitulo() {return titulo;}

    public Plataforma getPlataforma() {return plataforma;}

    public Precio getPrecio() {return precio;}

    public int getStock() {return stock;}

    public EstadoOferta getEstado() {return estado;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Oferta other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
