package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.infrastructure.rest.dto.LoginDTO;
import com.uniquindio.ecommerce.infrastructure.rest.dto.RegistroUsuarioDTO;
import com.uniquindio.ecommerce.infrastructure.rest.dto.TokenDTO;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.UsuarioEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.ecommerce.infrastructure.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioJpaDataRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils,
                          PasswordEncoder passwordEncoder,
                          UsuarioJpaDataRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@Valid @RequestBody RegistroUsuarioDTO dto) {
        if (usuarioRepository.existePorEmail(dto.email())) {
            return ResponseEntity.badRequest().body("El email ya se encuentra registrado.");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(dto.rol() != null ? dto.rol() : "COMPRADOR");

        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente.");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        UsuarioEntity usuario = usuarioRepository.buscarPorEmail(dto.email()).orElseThrow();
        String token = jwtUtils.generarToken(usuario.getEmail(), usuario.getRol());

        return ResponseEntity.ok(new TokenDTO(token, usuario.getEmail()));
    }
}