package com.example.proyecto1.presentation.puestos;

import com.example.proyecto1.logic.Caracteristica;
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
//    @GetMapping("/nuevo")
//    public String mostrarFormulario(Model model, Principal principal) {
//
//        Empresa empresa = empresaService.buscarPorId(principal.getName());
//
//        // Necesitás la lista de características para el dropdown
//        // La pedís al CaracteristicaService de tu compañero
//        // Por ahora la dejás vacía hasta que él la tenga lista
//        //List<Caracteristica> caracteristicas = caracteristicaService.findAll();
//
//        model.addAttribute("empresa",        empresa);
//        model.addAttribute("caracteristicas", caracteristicas);
//        return "puestos/nuevo-puesto";
//    }

//    @PostMapping("/nuevo")
//    public String guardarPuesto(
//            @RequestParam String descripcion,
//            @RequestParam Double salario,
//            @RequestParam String tipoPuesto,
//            @RequestParam List<Integer> idCaracteristicas,
//            @RequestParam List<Integer> niveles,
//            Principal principal) {
//
//        // 1. Crear el puesto y obtener su ID generado
//        Puesto puesto = puestoService.crearPuesto(
//                principal.getName(), descripcion, salario, tipoPuesto
//        );
//
//        // 2. Agregar cada característica con su nivel
//        for (int i = 0; i < idCaracteristicas.size(); i++) {
//            puestoService.agregarCaracteristica(
//                    puesto.getId(),
//                    idCaracteristicas.get(i),
//                    niveles.get(i)
//            );
//        }
//
//        return "redirect:/empresa/puestos";
//    }
}
