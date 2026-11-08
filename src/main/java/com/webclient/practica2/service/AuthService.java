package com.webclient.practica2.service;

import com.webclient.practica2.dto.AuthRequest;
import com.webclient.practica2.dto.AuthResponse;
import com.webclient.practica2.dto.RegistroRequest;
import com.webclient.practica2.exception.BadRequestException;
import com.webclient.practica2.model.Rol;
import com.webclient.practica2.model.Usuario;
import com.webclient.practica2.repository.UsuarioRepository;
import com.webclient.practica2.security.CustomUserDetailsService;
import com.webclient.practica2.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse login(AuthRequest request) {
        // Autentica contra Spring Security (lanza BadCredentialsException si es erróneo)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        String token = jwtService.generarToken(userDetails);

        return new AuthResponse(
                token,
                usuario.getUsername(),
                usuario.getRol().name(),
                "Autenticación exitosa. Use el token en la cabecera 'Authorization: Bearer <token>'."
        );
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("El nombre de usuario ya está registrado");
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El correo electrónico ya está registrado");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setNombreCompleto(request.getNombreCompleto());
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setRol(request.getRol() != null ? request.getRol() : Rol.ROLE_CONSULTA);
        nuevoUsuario.setActivo(true);

        usuarioRepository.save(nuevoUsuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(nuevoUsuario.getUsername());
        String token = jwtService.generarToken(userDetails);

        return new AuthResponse(
                token,
                nuevoUsuario.getUsername(),
                nuevoUsuario.getRol().name(),
                "Usuario registrado exitosamente."
        );
    }
}
