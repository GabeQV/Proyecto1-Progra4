package com.example.proyecto1.presentation.empresas;

import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class EmpresaController {

    private final Service service;

    public EmpresaController(Service service) {
        this.service = service;
    }


    @GetMapping("/empresa/dashboard")
    public String dashboard(Model model, Principal principal) {

        Empresa empresa = service.buscarPorIdEmp(principal.getName());

        model.addAttribute("empresa", empresa);
        return "empresas/dashboard";
    }
    @GetMapping("/RegistroEmpresa")
    public String Emp_register() {
        return "registro/registro-empresa";
    }

    @PostMapping("/SaveEmpresa")
    public String registrar(
            @RequestParam String id,
            @RequestParam String nombre,
            @RequestParam String localizacion,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String descripcion,
            @RequestParam String clave,
            RedirectAttributes redirectAttrs) {
        try {
            service.registrarEmpresa(id,correo,clave, nombre, localizacion,
                    telefono, descripcion);
            redirectAttrs.addFlashAttribute("exito",
                    "Registro exitoso. Tu cuenta está pendiente de aprobación.");
            return "redirect:/RegistroEmpresa";
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/RegistroEmpresa";
        }
    }

}

