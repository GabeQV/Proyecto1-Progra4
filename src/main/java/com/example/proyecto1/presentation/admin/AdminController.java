package com.example.proyecto1.presentation.admin;

import com.example.proyecto1.data.UsuarioRepository;
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

    public AdminController(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
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
}