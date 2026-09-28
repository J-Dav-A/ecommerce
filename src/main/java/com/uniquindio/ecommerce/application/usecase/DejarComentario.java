package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;

import java.util.UUID;

/**
 * Caso de uso: DejarComentario.
 *
 * Permite a un comprador dejar un comentario y una calificación
 * sobre un producto adquirido.
 */
public class DejarComentario {

    private final CompraRepository compraRepository;
    private final ComentarioRepository comentarioRepository;

    public DejarComentario(
            CompraRepository compraRepository,
            ComentarioRepository comentarioRepository
    ) {
        this.compraRepository = compraRepository;
        this.comentarioRepository = comentarioRepository;
    }

    /**
     * Ejecuta el caso de uso para dejar un comentario.
     *
     * @param compraId identificador de la compra.
     * @param compradorId identificador del comprador.
     * @param contenido contenido del comentario.
     * @param calificacion calificación del producto entre 1 y 5.
     * @return comentario creado.
     */
    public Comentario ejecutar(
            String compraId,
            String compradorId,
            String contenido,
            int calificacion
    ) {

        // Validación: debe indicarse la compra.
        if (compraId == null || compraId.isBlank()) {
            throw new ReglaDominioException(
                    "Debe indicarse la compra"
            );
        }

        // Validación: debe indicarse el comprador.
        if (compradorId == null || compradorId.isBlank()) {
            throw new ReglaDominioException(
                    "Debe indicarse el comprador"
            );
        }

        // La compra debe existir.
        Compra compra = compraRepository.obtenerPorId(compraId)
                .orElseThrow(() ->
                        new ReglaDominioException(
                                "La compra no existe"
                        )
                );

        /*
         * Regla 4:
         * Un comprador no puede calificar un producto
         * sin haberlo comprado.
         *
         * La compra debe pertenecer al comprador que
         * intenta realizar el comentario y debe estar completada.
         */
        if (!compra.getCompradorId().equals(compradorId)
                || compra.getEstado() != EstadoCompra.COMPLETADA) {

            throw new ReglaDominioException(
                    "El comprador debe tener una compra completada "
                            + "del producto para poder calificarlo"
            );
        }

        /*
         * Regla 6:
         * Una compra solo puede tener un comentario.
         */
        if (comentarioRepository.existePorCompraId(compraId)) {
            throw new ReglaDominioException(
                    "La compra ya tiene un comentario"
            );
        }

        // Las validaciones propias del contenido y la calificación
        // permanecen dentro de la entidad Comentario.
        Comentario comentario = Comentario.crear(
                UUID.randomUUID(),
                compraId,
                contenido,
                calificacion
        );

        // Se guarda el comentario una vez superadas
        // todas las reglas correspondientes.
        comentarioRepository.guardar(comentario);

        return comentario;
    }
}