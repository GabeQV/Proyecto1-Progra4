package com.example.proyecto1.presentation.empresas;

import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.service.EmpresaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;

    // Constructor igual que usás en tus Services
    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    // Dashboard

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        Empresa empresa = empresaService.buscarPorId(principal.getName());

        model.addAttribute("empresa", empresa);
        return "empresas/dashboard";
    }

}

