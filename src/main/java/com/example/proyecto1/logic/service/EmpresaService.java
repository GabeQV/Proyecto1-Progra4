package com.example.proyecto1.logic.service;

import com.example.proyecto1.data.EmpresaRepository;
import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepo;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    // Constructor igual que tu RegistroService
    public EmpresaService(EmpresaRepository er, UsuarioRepository ur, PasswordEncoder pe) {
        this.empresaRepo   = er;
        this.usuarioRepo   = ur;
        this.passwordEncoder = pe;
    }

    // ── Registro público de empresa
    @Transactional
    public void registrarEmpresa(String id, String correo, String clave, String nombre, String localizacion, String telefono, String descripcion) {
        if (usuarioRepo.existsById(id)) {
            throw new IllegalArgumentException("Ya existe un usuario con esa identificación.");
        }
        // Crear usuario base — igual que en registrarOferente
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setCorreo(correo);
        usuario.setClave(passwordEncoder.encode(clave));
        usuario.setRolUsuario("EMPRESA");
        usuario.setActivo(false);  // espera aprobación del admin
        usuarioRepo.save(usuario);

        // Crear empresa vinculada — igual que creás el Oferente
        Empresa empresa = new Empresa();
        empresa.setUsuario(usuario);
        empresa.setNombre(nombre);
        empresa.setLocalizacion(localizacion);
        empresa.setTelefono(telefono);
        empresa.setDescripcion(descripcion);
        empresa.setAprobado(false);
        empresaRepo.save(empresa);
    }

    // ── Aprobar empresa
    @Transactional
    public void aprobarEmpresa(String id) {
        empresaRepo.findById(id).ifPresent(empresa -> {
            empresa.setAprobado(true);

            Usuario usuario = empresa.getUsuario();
            usuario.setActivo(true);  // ahora puede loguearse

            usuarioRepo.save(usuario);
        });
    }

    // ── Buscar empresa del usuario logueado ───────────────────
    // Lo usás en el dashboard para mostrar el nombre de la empresa
    public Empresa buscarPorId(String id) {
        return empresaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
    }
}