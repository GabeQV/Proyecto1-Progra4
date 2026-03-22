package com.example.proyecto1.dev;

import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.Oferente;
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

        if (usuarioRepository.count() == 0) {
            String password = passwordEncoder.encode("123");

            // --- 1. Usuario Administrador ---
            Usuario admin = new Usuario();
            admin.setId("admin");
            admin.setCorreo("admin@bolsaempleo.com");
            admin.setClave(password);
            admin.setRolUsuario("ADMIN");
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("Creado usuario: admin");

            // --- 2. Empresa Aprobada ---
            Usuario userEmpresaAprobada = new Usuario();
            userEmpresaAprobada.setId("empresa1");
            userEmpresaAprobada.setCorreo("empresa1@test.com");
            userEmpresaAprobada.setClave(password);
            userEmpresaAprobada.setRolUsuario("EMPRESA");
            userEmpresaAprobada.setActivo(true);

            Empresa empresaAprobada = new Empresa();
            // No seteamos el ID aquí. Se derivará automáticamente.
            empresaAprobada.setNombre("Tech Solutions Inc.");
            empresaAprobada.setAprobado(true);

            // Vinculamos en ambas direcciones
            userEmpresaAprobada.setEmpresa(empresaAprobada);
            empresaAprobada.setUsuario(userEmpresaAprobada);

            usuarioRepository.save(userEmpresaAprobada); // Solo guardamos el usuario
            System.out.println("Creada empresa aprobada: empresa1");

            // --- 3. Empresa Pendiente ---
            Usuario userEmpresaPendiente = new Usuario();
            userEmpresaPendiente.setId("empresa2");
            userEmpresaPendiente.setCorreo("empresa2@test.com");
            userEmpresaPendiente.setClave(password);
            userEmpresaPendiente.setRolUsuario("EMPRESA");
            userEmpresaPendiente.setActivo(false);

            Empresa empresaPendiente = new Empresa();
            empresaPendiente.setNombre("Innovatec");
            empresaPendiente.setAprobado(false);

            // Vinculamos en ambas direcciones
            userEmpresaPendiente.setEmpresa(empresaPendiente);
            empresaPendiente.setUsuario(userEmpresaPendiente);

            usuarioRepository.save(userEmpresaPendiente); // Solo guardamos el usuario
            System.out.println("Creada empresa pendiente: empresa2");

            // --- 4. Oferente Pendiente ---
            Usuario userOferentePendiente = new Usuario();
            userOferentePendiente.setId("oferente2");
            userOferentePendiente.setCorreo("oferente2@test.com");
            userOferentePendiente.setClave(password);
            userOferentePendiente.setRolUsuario("OFERENTE");
            userOferentePendiente.setActivo(false);

            Oferente oferentePendiente = new Oferente();
            oferentePendiente.setNombre("Carlos");
            oferentePendiente.setPrimerApellido("Rojas");
            oferentePendiente.setAprobado(false);

            // Vinculamos en ambas direcciones
            userOferentePendiente.setOferente(oferentePendiente);
            oferentePendiente.setUsuario(userOferentePendiente);

            usuarioRepository.save(userOferentePendiente); // Solo guardamos el usuario
            System.out.println("Creado oferente pendiente: oferente2");

            System.out.println("Datos de prueba cargados exitosamente.");
        } else {
            System.out.println("La base de datos ya contiene datos.");
        }
    }
}