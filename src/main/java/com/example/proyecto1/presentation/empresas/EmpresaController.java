package com.example.proyecto1.presentation.empresas;

import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {

    private final Service service;

    public EmpresaController(Service service) {
        this.service = service;
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        Empresa empresa = service.buscarPorIdEmp(principal.getName());

        model.addAttribute("empresa", empresa);
        return "empresas/dashboard";
    }

}

