package com.example.proyecto1.presentation.puestos;

import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.Puesto;
import com.example.proyecto1.logic.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/empresa/puestos")
public class PuestosController {

    private final Service service;

    @Autowired
    public PuestosController(Service service) {
        this.service = service;
    }

    // GET /empresa/puestos
    @GetMapping
    public String listarPuestos(Model model, Principal principal) {
        Empresa empresa = service.buscarPorIdEmp(principal.getName());
        List<Puesto> puestos = service.getPuestosDeEmpresa(principal.getName());

        model.addAttribute("empresa", empresa);
        model.addAttribute("puestos", puestos);
        return "puestos/puestos";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Integer id) {
        service.desactivar(id);
        return "redirect:/empresa/puestos";
    }

    // GET /empresa/puestos/nuevo
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model, Principal principal) {
        Empresa empresa = service.buscarPorIdEmp(principal.getName());

        model.addAttribute("empresa", empresa);
        model.addAttribute("caracteristicas", service.getAllCaracteristicas());
        return "puestos/nuevo-puesto";
    }

    // POST /empresa/puestos/nuevo
    @PostMapping("/nuevo")
    public String guardarPuesto(
            @RequestParam String descripcion,
            @RequestParam Double salario,
            @RequestParam String tipoPuesto,
            @RequestParam List<Integer> idCaracteristicas,
            @RequestParam List<Integer> niveles,
            Principal principal) {

        Puesto puesto = service.crearPuesto(principal.getName(), descripcion, salario, tipoPuesto);

        for (int i = 0; i < idCaracteristicas.size(); i++) {
            service.agregarCaracteristicaAPuesto(
                    puesto.getId(),
                    idCaracteristicas.get(i),
                    niveles.get(i)
            );
        }

        return "redirect:/empresa/puestos";
    }
}