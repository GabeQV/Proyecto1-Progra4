package com.example.proyecto1.presentation.admin;

import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.logic.Usuario;
import com.example.proyecto1.logic.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AdminController(AdminService adminService, UsuarioRepository usuarioRepository) {
        this.adminService = adminService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        Usuario admin = usuarioRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "No encontrado");
        return "admin/dashboard";
    }

    @GetMapping("/empresas-pendientes")
    public String showEmpresasPendientes(Model model, Principal principal) {
        Usuario admin = usuarioRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "No encontrado");
        model.addAttribute("empresas", adminService.getEmpresasPendientes());
        return "admin/empresas-pendientes";
    }

    @PostMapping("/empresas/aprobar/{empresaId}")
    public String aprobarEmpresa(@PathVariable String empresaId) {
        adminService.aprobarEmpresa(empresaId);
        return "redirect:/admin/empresas-pendientes";
    }

    @GetMapping("/oferentes-pendientes")
    public String showOferentesPendientes(Model model, Principal principal) {
        Usuario admin = usuarioRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "No encontrado");
        model.addAttribute("oferentes", adminService.getOferentesPendientes());
        return "admin/oferentes-pendientes";
    }

    @PostMapping("/oferentes/aprobar/{oferenteId}")
    public String aprobarOferente(@PathVariable String oferenteId) {
        adminService.aprobarOferente(oferenteId);
        return "redirect:/admin/oferentes-pendientes";
    }
}