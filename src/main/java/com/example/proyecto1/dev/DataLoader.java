package com.example.proyecto1.dev;

import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Cargando datos de prueba...");

        // Solo cargar datos si no existen para evitar duplicados
        if (usuarioRepository.count() == 0) {
            // ---- Contraseña para todos los usuarios: "123" ----
            String password = passwordEncoder.encode("123");

            // 1. Usuario Administrador
            Usuario admin = new Usuario();
            admin.setId("admin");
            admin.setCorreo("admin@bolsaempleo.com");
            admin.setClave(password);
            admin.setRolUsuario("ADMIN");
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("Creado usuario: admin, rol: ADMIN, clave: 123");

            // 2. Usuario Empresa (Aprobado)
            Usuario empresaAprobada = new Usuario();
            empresaAprobada.setId("empresa1");
            empresaAprobada.setCorreo("empresa1@test.com");
            empresaAprobada.setClave(password);
            empresaAprobada.setRolUsuario("EMPRESA");
            empresaAprobada.setActivo(true); // Esta empresa ya fue aprobada
            usuarioRepository.save(empresaAprobada);
            System.out.println("Creado usuario: empresa1, rol: EMPRESA, clave: 123");

            // 3. Usuario Empresa (Pendiente de aprobación)
            Usuario empresaPendiente = new Usuario();
            empresaPendiente.setId("empresa2");
            empresaPendiente.setCorreo("empresa2@test.com");
            empresaPendiente.setClave(password);
            empresaPendiente.setRolUsuario("EMPRESA");
            empresaPendiente.setActivo(false); // Esta empresa está pendiente de aprobación
            usuarioRepository.save(empresaPendiente);
            System.out.println("Creado usuario: empresa2, rol: EMPRESA, clave: 123 (PENDIENTE)");


            // 4. Usuario Oferente (Aprobado)
            Usuario oferenteAprobado = new Usuario();
            oferenteAprobado.setId("oferente1");
            oferenteAprobado.setCorreo("oferente1@test.com");
            oferenteAprobado.setClave(password);
            oferenteAprobado.setRolUsuario("OFERENTE");
            oferenteAprobado.setActivo(true); // Este oferente ya fue aprobado
            usuarioRepository.save(oferenteAprobado);
            System.out.println("Creado usuario: oferente1, rol: OFERENTE, clave: 123");

            // 5. Usuario Oferente (Pendiente de aprobación)
            Usuario oferentePendiente = new Usuario();
            oferentePendiente.setId("oferente2");
            oferentePendiente.setCorreo("oferente2@test.com");
            oferentePendiente.setClave(password);
            oferentePendiente.setRolUsuario("OFERENTE");
            oferentePendiente.setActivo(false); // Este oferente está pendiente
            usuarioRepository.save(oferentePendiente);
            System.out.println("Creado usuario: oferente2, rol: OFERENTE, clave: 123 (PENDIENTE)");


            System.out.println("Datos de prueba cargados exitosamente.");
        } else {
            System.out.println("La base de datos ya contiene datos, no se cargaron datos de prueba.");
        }
    }
}