package com.example.proyecto1.dev;

import com.example.proyecto1.data.*;
import com.example.proyecto1.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByCorreo("admin@bolsaempleo.com").isEmpty()) {
            System.out.println("Creando usuario administrador por defecto...");
            String password = passwordEncoder.encode("admin123");
            createAdmin("admin", "admin@bolsaempleo.com", password);
            System.out.println("Usuario administrador creado.");
        } else {
            System.out.println("El usuario administrador ya existe. No se creará de nuevo.");
        }
    }


    private void createAdmin(String id, String correo, String clave) {
        Usuario admin = new Usuario();
        admin.setId(id);
        admin.setCorreo(correo);
        admin.setClave(clave);
        admin.setRolUsuario("ADMIN");
        admin.setActivo(true);
        usuarioRepository.save(admin);
    }
}