package com.example.proyecto1.presentation.puestos;

import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.Puesto;
import com.example.proyecto1.logic.service.EmpresaService;
import com.example.proyecto1.logic.service.PuestoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/empresa/puestos")  // ← base de todas las rutas de puestos
public class PuestosController {

    private final PuestoService puestoService;
    private final EmpresaService empresaService;

    public PuestosController(PuestoService puestoService,
                             EmpresaService empresaService) {
        this.puestoService  = puestoService;
        this.empresaService = empresaService;
    }

    // GET /empresa/puestos
    @GetMapping
    public String listarPuestos(Model model, Principal principal) {
        Empresa empresa = empresaService.buscarPorId(principal.getName());
        List<Puesto> puestos = puestoService.getPuestosDeEmpresa(principal.getName());

        model.addAttribute("empresa", empresa);
        model.addAttribute("puestos", puestos);
        return "puestos/puestos";
    }

    // POST /empresa/puestos/{id}/desactivar
    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Integer id) {
        puestoService.desactivar(id);
        return "redirect:/empresa/puestos";
    }
}
