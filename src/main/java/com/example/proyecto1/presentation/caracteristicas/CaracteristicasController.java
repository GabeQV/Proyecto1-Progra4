package com.example.proyecto1.presentation.caracteristicas;

import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.logic.Caracteristica;
import com.example.proyecto1.logic.Usuario;
import com.example.proyecto1.logic.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

@Controller
public class CaracteristicasController {

    private final AdminService adminService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CaracteristicasController(AdminService adminService, UsuarioRepository usuarioRepository) {
        this.adminService = adminService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/admin/caracteristicas")
    public String showCaracteristicas(@RequestParam(required = false) Integer actualId, Model model, Principal principal) {
        Usuario admin = usuarioRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "No encontrado");

        Map<String, Object> viewData = adminService.getCaracteristicasViewData(actualId);
        model.addAllAttributes(viewData);

        model.addAttribute("nuevaCaracteristica", new Caracteristica());

        return "admin/caracteristicas";
    }

    @PostMapping("/admin/caracteristicas/agregar")
    public String agregarCaracteristica(@ModelAttribute Caracteristica nuevaCaracteristica) {
        if (nuevaCaracteristica.getIdPadre() != null && nuevaCaracteristica.getIdPadre().getId() == 0) {
            nuevaCaracteristica.setIdPadre(null);
        }
        adminService.addCaracteristica(nuevaCaracteristica);
        return "redirect:/admin/caracteristicas";
    }
}