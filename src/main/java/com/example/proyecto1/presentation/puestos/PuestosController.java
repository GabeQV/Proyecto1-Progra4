package com.example.proyecto1.presentation.puestos;

import com.example.proyecto1.data.CaracteristicaRepository;
import com.example.proyecto1.data.PuestoCaracteristicaRepository;
import com.example.proyecto1.logic.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/empresa/puestos")  // ← base de todas las rutas de puestos
public class PuestosController {

    private final Service service;
    private final CaracteristicaRepository caracteristicaRepository;

    public PuestosController(Service service,CaracteristicaRepository caracteristicaRepository) {
        this.caracteristicaRepository=caracteristicaRepository;
        this.service  = service;
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
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model, Principal principal) {

        Empresa empresa = service.buscarPorIdEmp(principal.getName());
        List<Caracteristica> caracteristicas = caracteristicaRepository.findAll();

        model.addAttribute("empresa",        empresa);
        model.addAttribute("caracteristicas", caracteristicas);
        return "puestos/nuevo-puesto";
    }

    @PostMapping("/nuevo")
    public String guardarPuesto(
            @RequestParam String descripcion,
            @RequestParam Double salario,
            @RequestParam String tipoPuesto,
            @RequestParam List<Integer> idCaracteristicas,
            @RequestParam List<Integer> niveles,
            Principal principal) {

        // 1. Crear el puesto y obtener su ID generado
        Puesto puesto = service.crearPuesto(principal.getName(), descripcion, salario, tipoPuesto);

        // 2. Agregar cada característica con su nivel
        for (int i = 0; i < idCaracteristicas.size(); i++) {
            service.agregarCaracteristica(
                    puesto.getId(),
                    idCaracteristicas.get(i),
                    niveles.get(i)
            );
        }

        return "redirect:/empresa/puestos";
    }
}
