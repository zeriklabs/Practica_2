package com.webclient.practica2.config;

import com.webclient.practica2.model.*;
import com.webclient.practica2.repository.AtletaRepository;
import com.webclient.practica2.repository.EntrenadorRepository;
import com.webclient.practica2.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final AtletaRepository atletaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            EntrenadorRepository entrenadorRepository,
            AtletaRepository atletaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.atletaRepository = atletaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Inicializar usuarios si la base de datos está vacía
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "Administrador General",
                    "admin@deportes.com",
                    Rol.ROLE_ADMIN
            );
            usuarioRepository.save(admin);

            Usuario consulta = new Usuario(
                    "consulta",
                    passwordEncoder.encode("consulta123"),
                    "Usuario de Consulta",
                    "consulta@deportes.com",
                    Rol.ROLE_CONSULTA
            );
            usuarioRepository.save(consulta);
        }

        // Inicializar entrenadores y atletas de prueba si no existen
        if (entrenadorRepository.count() == 0) {
            Entrenador e1 = new Entrenador(
                    "Carlos",
                    "Mendoza",
                    "Atletismo",
                    10,
                    "carlos.mendoza@deportes.com",
                    "+52 5512345678",
                    EstadoEntrenador.ACTIVO
            );
            entrenadorRepository.save(e1);

            Entrenador e2 = new Entrenador(
                    "Laura",
                    "García",
                    "Natación",
                    8,
                    "laura.garcia@deportes.com",
                    "+52 5587654321",
                    EstadoEntrenador.INACTIVO
            );
            entrenadorRepository.save(e2);

            Entrenador e3 = new Entrenador(
                    "Roberto",
                    "Morales",
                    "Boxeo",
                    15,
                    "roberto.morales@deportes.com",
                    "+52 5599887766",
                    EstadoEntrenador.ACTIVO
            );
            entrenadorRepository.save(e3);

            // Atletas vinculados
            Atleta a1 = new Atleta("Mateo", "López", 19, "Juvenil", e1);
            Atleta a2 = new Atleta("Sofía", "Paredes", 22, "Senior", e1);
            Atleta a3 = new Atleta("Diego", "Ramírez", 25, "Elite", e3);

            atletaRepository.save(a1);
            atletaRepository.save(a2);
            atletaRepository.save(a3);
        }
    }
}

