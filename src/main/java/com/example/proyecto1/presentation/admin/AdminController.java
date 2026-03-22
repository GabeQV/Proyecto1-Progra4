package com.example.proyecto1.presentation.admin;

import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.data.OferenteRepository;
import com.example.proyecto1.logic.Oferente;
import com.example.proyecto1.logic.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.proyecto1.data.EmpresaRepository;
import com.example.proyecto1.logic.Empresa;
import java.util.List;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;

    public AdminController(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository, OferenteRepository oferenteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.oferenteRepository = oferenteRepository;
    }


    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        String username = principal.getName();
        Usuario admin = usuarioRepository.findById(username).orElse(null);

        // Pasamos el correo y el nombre de usuario a la vista
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "No encontrado");
        model.addAttribute("username", username);

        return "admin/dashboard";
    }

    @GetMapping("/empresas-pendientes")
    public String showEmpresasPendientes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        Usuario admin = usuarioRepository.findById(currentUserName).orElse(null);
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "No encontrado");

        List<Empresa> empresasPendientes = empresaRepository.findByAprobadoFalse();
        model.addAttribute("empresas", empresasPendientes);

        return "admin/empresas-pendientes";
    }

    @PostMapping("/empresas/aprobar/{empresaId}")
    public String aprobarEmpresa(@PathVariable String empresaId) {
        empresaRepository.findById(empresaId).ifPresent(empresa -> {
            Usuario usuario = empresa.getUsuario();
            if (usuario != null) {
                empresa.setAprobado(true);
                empresaRepository.save(empresa);

                usuario.setActivo(true);
                // String nuevaClave = "nuevaClave123";
                // usuario.setClave(passwordEncoder.encode(nuevaClave));
                usuarioRepository.save(usuario);
            }
        });
        return "redirect:/admin/empresas-pendientes";
    }

    @GetMapping("/oferentes-pendientes")
    public String showOferentesPendientes(Model model, Principal principal) {
        // Obtenemos la información del admin para el header
        Usuario admin = usuarioRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "No encontrado");

        // Buscamos y añadimos la lista de oferentes pendientes al modelo
        List<Oferente> oferentesPendientes = oferenteRepository.findByAprobadoFalse();
        model.addAttribute("oferentes", oferentesPendientes);

        return "admin/oferentes-pendientes"; // Nueva vista que crearemos
    }

    @PostMapping("/oferentes/aprobar/{oferenteId}")
    public String aprobarOferente(@PathVariable String oferenteId) {
        // Buscamos al oferente por su ID
        oferenteRepository.findById(oferenteId).ifPresent(oferente -> {
            // Buscamos al usuario asociado
            Usuario usuario = oferente.getUsuario();
            if (usuario != null) {
                // Actualizamos el estado en ambas entidades
                oferente.setAprobado(true);
                usuario.setActivo(true);

                // Guardamos los cambios. Gracias al Cascade, solo necesitamos guardar el usuario.
                usuarioRepository.save(usuario);
            }
        });

        // Redirigimos de vuelta a la lista de pendientes
        return "redirect:/admin/oferentes-pendientes";
    }
}