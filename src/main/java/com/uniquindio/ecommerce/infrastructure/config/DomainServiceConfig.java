package com.uniquindio.ecommerce.infrastructure.config;

import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.service.VerificadorDeCompra;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public VerificadorDeCompra verificadorDeCompra(CompraRepository compraRepository) {
        return new VerificadorDeCompra(compraRepository);
    }
}