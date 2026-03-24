
package com.example.proyecto1.logic.service;

import com.example.proyecto1.data.EmpresaRepository;
import com.example.proyecto1.data.OferenteRepository;
import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.Oferente;
import com.example.proyecto1.logic.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistroService {


    private final UsuarioRepository usuarioRepo;
    private final OferenteRepository oferenteRepo;
    private final EmpresaRepository empresaRepo;
    private final PasswordEncoder passwordEncoder;


    public RegistroService(UsuarioRepository ur, OferenteRepository or, EmpresaRepository er, PasswordEncoder pe) {
        this.usuarioRepo   = ur;
        this.oferenteRepo  = or;
        this.empresaRepo   = er;
        this.passwordEncoder = pe;
    }


    @Transactional
    public void registrarOferente(String id, String correo, String clave, String nombre, String primerApellido, String segundoApellido, String nacionalidad, String telefono, String residencia) {

        if (usuarioRepo.existsById(id)) {
            throw new IllegalArgumentException("Ya existe un usuario con esa identificación.");
        }

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setCorreo(correo);
        usuario.setClave(passwordEncoder.encode(clave));
        usuario.setRolUsuario("OFERENTE");
        usuario.setActivo(false);

        Oferente oferente = new Oferente();
        oferente.setUsuario(usuario);
        oferente.setNombre(nombre);
        oferente.setPrimerApellido(primerApellido);
        oferente.setSegundoApellido(segundoApellido);
        oferente.setNacionalidad(nacionalidad);
        oferente.setTelefono(telefono);
        oferente.setResidencia(residencia);
        oferente.setAprobado(false);

        oferenteRepo.save(oferente);
    }


    @Transactional
    public void aprobarOferente(String id) {
        oferenteRepo.findById(id).ifPresent(oferente -> {
            oferente.setAprobado(true);

            Usuario usuario = oferente.getUsuario();
            usuario.setActivo(true);

            usuarioRepo.save(usuario);
        });
    }


    public List<Oferente> obtenerOferentesPendientes() {
        return oferenteRepo.findByAprobadoFalse();
    }

    public List<Empresa> obtenerEmpresasPendientes() {
        return empresaRepo.findByAprobadoFalse();
    }
}