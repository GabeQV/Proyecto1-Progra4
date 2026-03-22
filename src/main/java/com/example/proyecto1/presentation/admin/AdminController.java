package com.example.proyecto1.presentation.admin;

import com.example.proyecto1.data.CaracteristicaRepository;
import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.data.OferenteRepository;
import com.example.proyecto1.logic.Caracteristica;
import com.example.proyecto1.logic.Oferente;
import com.example.proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.proyecto1.data.EmpresaRepository;
import com.example.proyecto1.logic.Empresa;
import java.util.List;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final EmpresaRepository empresaRepository;
    @Autowired
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
        Usuario admin = usuarioRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "No encontrado");

        List<Oferente> oferentesPendientes = oferenteRepository.findByAprobadoFalse();
        model.addAttribute("oferentes", oferentesPendientes);

        return "admin/oferentes-pendientes";
    }

    @PostMapping("/oferentes/aprobar/{oferenteId}")
    public String aprobarOferente(@PathVariable String oferenteId) {

        oferenteRepository.findById(oferenteId).ifPresent(oferente -> {
            Usuario usuario = oferente.getUsuario();
            if (usuario != null) {
                oferente.setAprobado(true);
                usuario.setActivo(true);

                usuarioRepository.save(usuario);
            }
        });

        return "redirect:/admin/oferentes-pendientes";
    }


}