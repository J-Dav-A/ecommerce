package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;

import java.util.Objects;

public class Oferta {

    private final String id;
    private final String vendedorId;
    private final String titulo;
    private Plataforma plataforma;
    private Precio precio;
    private int stock;
    private EstadoOferta estado;

    private Oferta(String id, String vendedorId, String titulo, Precio precio, int stock, Plataforma plataforma, EstadoOferta estado) {
        this.id = id;
        this.vendedorId = vendedorId;
        this.titulo = titulo;
        this.precio = precio;
        this.stock = stock;
        this.plataforma = plataforma;
        this.estado = estado;
    }

    public static Oferta crear(String id, String vendedorId, String titulo, Precio precio, int stock) {
        if (id == null || id.isBlank()) throw new ReglaDominioException("La oferta requiere identificador");
        if (vendedorId == null || vendedorId.isBlank()) throw new ReglaDominioException("La oferta requiere vendedor");
        if (titulo == null || titulo.isBlank()) throw new ReglaDominioException("El título es obligatorio");
        if (precio == null) throw new ReglaDominioException("El precio es obligatorio");
        if (stock < 0) throw new ReglaDominioException("El stock no puede ser negativo");

        return new Oferta(id, vendedorId, titulo, precio, stock, null, EstadoOferta.BORRADOR);
    }

    public static Oferta reconstruir(String id, String vendedorId, String titulo, Precio precio, int stock, Plataforma plataforma, EstadoOferta estado) {
        return new Oferta(id, vendedorId, titulo, precio, stock, plataforma, estado);
    }

    public void definirPlataforma(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    public void publicar() {
        if (plataforma == null) throw new ReglaDominioException("Se requiere una plataforma para publicar");
        if (precio == null) throw new ReglaDominioException("Se requiere un precio para publicar");
        if (stock <= 0) throw new ReglaDominioException("No se puede publicar sin stock disponible");
        if (estado == EstadoOferta.ELIMINADA) throw new ReglaDominioException("No se puede publicar una oferta eliminada");
        this.estado = EstadoOferta.PUBLICADA;
    }

    public void descontarStock(int cantidad) {
        if (cantidad <= 0) throw new ReglaDominioException("La cantidad a descontar debe ser mayor a cero");
        if (cantidad > stock) throw new ReglaDominioException("Stock insuficiente");
        this.stock -= cantidad;
    }

    public void eliminar() {
        if (estado == EstadoOferta.ELIMINADA) throw new ReglaDominioException("La oferta ya se encuentra eliminada");
        this.estado = EstadoOferta.ELIMINADA;
    }

    public String getId() { return id; }
    public String getVendedorId() { return vendedorId; }
    public String getTitulo() { return titulo; }
    public Plataforma getPlataforma() { return plataforma; }
    public Precio getPrecio() { return precio; }
    public int getStock() { return stock; }
    public EstadoOferta getEstado() { return estado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Oferta otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}