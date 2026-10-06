package com.uniquindio.ecommerce.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;

public class Compra {

    private final String id;
    private final String ofertaId;
    private final String compradorId;
    private final Precio precioCongelado;
    private final LocalDateTime fechaCompra;
    private EstadoCompra estado;

    /**
     * Constructor privado.
     *
     * Regla 3:
     * Toda compra nace en estado PENDIENTE.
     *
     * Regla 4:
     * El precio queda congelado desde el momento
     * en que se crea la compra.
     */
    private Compra(
            String id,
            String ofertaId,
            String compradorId,
            Precio precioCongelado) {

        this.id = id;
        this.ofertaId = ofertaId;
        this.compradorId = compradorId;
        this.precioCongelado = precioCongelado;
        this.fechaCompra = LocalDateTime.now();

        // Regla 3:
        // Toda compra nace en estado PENDIENTE.
        this.estado = EstadoCompra.PENDIENTE;
    }

    /**
     * Crea una nueva compra.
     *
     * Regla 1:
     * La compra debe indicar el modelo y el comprador.
     *
     * Regla 2:
     * La compra debe tener un precio válido.
     */
    public static Compra realizar(
            String id,
            String ofertaId,
            String compradorId,
            Precio precioActualDeLaOferta) {

        // Regla 1:
        // No se puede crear una compra sin modelo o comprador.
        if (ofertaId == null || compradorId == null) {
            throw new ReglaDominioException(
                    "La compra debe indicar modelo y comprador"
            );
        }

        // Regla 2:
        // No se puede crear una compra sin un precio válido.
        if (precioActualDeLaOferta == null) {
            throw new ReglaDominioException(
                    "La compra debe tener un precio válido"
            );
        }

        // El precio recibido se guarda como precio congelado
        // para que futuras modificaciones del modelo no afecten
        // el valor de esta compra.
        return new Compra(
                id,
                ofertaId,
                compradorId,
                precioActualDeLaOferta
        );
    }

    // COMPORTAMIENTO DEL NEGOCIO

    /**
     * Confirma la compra.
     *
     * Regla 5:
     * Una compra solo puede pasar al siguiente estado
     * si la transición está permitida por el dominio.
     *
     * Regla 7:
     * Una compra reembolsada no puede modificarse.
     */
    public void confirmar() {

        // Regla 7:
        // Una compra reembolsada no puede modificarse.
        verificarQueNoEsteReembolsada();

        // Regla 5:
        // Se valida la transición antes de modificar el estado.
        verificarTransicion(EstadoCompra.COMPLETADA);

        // El estado solo cambia después de superar todas
        // las validaciones del dominio.
        this.estado = EstadoCompra.COMPLETADA;
    }

    /**
     * Solicita el reembolso de una compra.
     *
     * Regla 6:
     * Solo una compra COMPLETADA puede solicitar un reembolso.
     *
     * Regla 7:
     * Una compra reembolsada no puede modificarse.
     *
     * Regla 8:
     * El reembolso debe tener un motivo.
     */
    public void solicitarReembolso(String motivo) {

        // Regla 7:
        // Una compra reembolsada no puede volver a modificarse.
        verificarQueNoEsteReembolsada();

        // Regla 6:
        // Solo una compra COMPLETADA puede ser reembolsada.
        if (this.estado != EstadoCompra.COMPLETADA) {
            throw new ReglaDominioException(
                    "Solo se puede reembolsar una compra completada"
            );
        }

        // Regla 8:
        // Todo reembolso debe indicar el motivo de la solicitud.
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaDominioException(
                    "El reembolso exige indicar un motivo"
            );
        }

        // Se valida que la transición hacia REEMBOLSADA
        // esté permitida por el estado actual.
        verificarTransicion(EstadoCompra.REEMBOLSADA);

        // El estado se modifica únicamente después
        // de superar todas las reglas.
        this.estado = EstadoCompra.REEMBOLSADA;
    }

    // VERIFICACIONES INTERNAS DEL DOMINIO


    /**
     * Verifica que la compra todavía pueda modificarse.
     *
     * Regla 7:
     * Una compra reembolsada no puede modificarse.
     */
    private void verificarQueNoEsteReembolsada() {

        // Si el estado actual es final, la compra
        // ya no puede sufrir modificaciones.
        if (this.estado.esFinal()) {
            throw new ReglaDominioException(
                    "Una compra reembolsada no puede modificarse"
            );
        }
    }

    /**
     * Verifica que una transición de estado sea válida.
     * Regla 5:
     * Las compras únicamente pueden realizar las
     * transiciones permitidas por su ciclo de vida.
     */
    private void verificarTransicion(EstadoCompra siguiente) {

        // Se consulta al Value Object EstadoCompra
        // si la transición solicitada está permitida.
        if (!this.estado.puedeTransicionarA(siguiente)) {
            throw new ReglaDominioException(
                    "No se puede pasar de "
                            + this.estado
                            + " a "
                            + siguiente
            );
        }
    }

    // ---------------------------------------------------------
    // CONSULTAS
    // ---------------------------------------------------------

    /**
     * Obtiene el estado actual de la compra.
     */
    public EstadoCompra getEstado() {
        return estado;
    }

    /**
     * Obtiene el precio congelado de la compra.
     * Regla 4:
     * Este es el precio que tenía el producto al momento
     * de realizar la compra.
     */
    public Precio getPrecioCongelado() {
        return precioCongelado;
    }

    /**
     * Obtiene el identificador de la compra.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el identificador de la oferta comprada.
     */
    public String getOfertaId() {
        return ofertaId;
    }

    /**
     * Obtiene el identificador del comprador.
     */
    public String getCompradorId() {
        return compradorId;
    }

    /**
     * Obtiene la fecha en la que se realizó la compra.
     */
    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    // IDENTIDAD DE LA ENTIDAD

    /**
     * Dos compras representan la misma entidad
     * cuando tienen el mismo identificador.
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Compra otra)) {
            return false;
        }

        return id.equals(otra.id);
    }

    /**
     * El hash se construye utilizando únicamente
     * el identificador de la entidad.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}