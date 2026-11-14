package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Oferta {

    public static final int MAX_IMAGENES = 10;
    private static final int MAX_LARGO_URL = 500;

    private final String id;
    private final String vendedorId;
    private String titulo;
    private Plataforma plataforma;
    private Precio precio;
    private int stock;
    private EstadoOferta estado;
    private final List<String> imagenes = new ArrayList<>();

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

    public static Oferta reconstruir(String id, String vendedorId, String titulo, Precio precio, int stock, Plataforma plataforma, EstadoOferta estado, List<String> imagenes) {
        Oferta oferta = new Oferta(id, vendedorId, titulo, precio, stock, plataforma, estado);
        if (imagenes != null) oferta.imagenes.addAll(imagenes);
        return oferta;
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

    public boolean perteneceA(String vendedorId) {
        return this.vendedorId.equals(vendedorId);
    }

    public void verificarDisponibleParaVenta() {
        if (estado != EstadoOferta.PUBLICADA) {
            throw new ReglaDominioException("La oferta no está disponible para la venta");
        }
    }

    // Edita los datos comerciales; el vendedor y el estado no cambian por esta vía
    public void editar(String titulo, Precio precio, int stock, Plataforma plataforma) {
        if (estado == EstadoOferta.ELIMINADA) throw new ReglaDominioException("No se puede editar una oferta eliminada");
        if (titulo == null || titulo.isBlank()) throw new ReglaDominioException("El título es obligatorio");
        if (precio == null) throw new ReglaDominioException("El precio es obligatorio");
        if (plataforma == null) throw new ReglaDominioException("La plataforma es obligatoria");
        if (stock < 0) throw new ReglaDominioException("El stock no puede ser negativo");
        if (estado == EstadoOferta.PUBLICADA && stock == 0) {
            throw new ReglaDominioException("Una oferta publicada necesita stock disponible");
        }
        this.titulo = titulo;
        this.precio = precio;
        this.stock = stock;
        this.plataforma = plataforma;
    }

    // Reemplaza la galería completa; cada imagen es una URL http/https
    public void definirImagenes(List<String> urls) {
        if (estado == EstadoOferta.ELIMINADA) throw new ReglaDominioException("No se puede editar una oferta eliminada");
        if (urls == null) throw new ReglaDominioException("La lista de imágenes es obligatoria");
        if (urls.size() > MAX_IMAGENES) {
            throw new ReglaDominioException("Una oferta admite máximo " + MAX_IMAGENES + " imágenes");
        }
        urls.forEach(Oferta::validarUrl);
        this.imagenes.clear();
        this.imagenes.addAll(urls);
    }

    private static void validarUrl(String url) {
        if (url == null || url.isBlank() || url.length() > MAX_LARGO_URL) {
            throw new ReglaDominioException("Cada imagen debe ser una URL de hasta " + MAX_LARGO_URL + " caracteres");
        }
        try {
            URI uri = new URI(url);
            String esquema = uri.getScheme();
            boolean valida = ("http".equalsIgnoreCase(esquema) || "https".equalsIgnoreCase(esquema)) && uri.getHost() != null;
            if (!valida) throw new ReglaDominioException("La imagen debe ser una URL http o https: " + url);
        } catch (java.net.URISyntaxException e) {
            throw new ReglaDominioException("La imagen debe ser una URL http o https: " + url);
        }
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
    public List<String> getImagenes() { return List.copyOf(imagenes); }

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