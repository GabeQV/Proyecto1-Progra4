package com.example.proyecto1.presentation.caracteristicas;

import com.example.proyecto1.logic.Caracteristica;
import com.example.proyecto1.logic.Service;
import com.example.proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class CaracteristicasController {

    private final Service service;

    @Autowired
    public CaracteristicasController(Service service) {
        this.service = service;
    }


    @GetMapping("/admin/caracteristicas")
    public String showCaracteristicasAdmin(
            @RequestParam(required = false) Integer actualId,
            Model model,
            Principal principal) {

        Usuario admin = service.buscarUsuarioPorId(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "No encontrado");

        if (actualId == null) {
            model.addAttribute("subCategorias", service.getCaracteristicasRaiz());
            model.addAttribute("titulo", "Categorías: raíces");
        } else {
            model.addAttribute("subCategorias", service.getSubCaracteristicas(actualId));
            Caracteristica actual = service.getCaracteristica(actualId);
            model.addAttribute("titulo", "Subcategorías de: " + (actual != null ? actual.getNombre() : ""));
        }

        model.addAttribute("breadcrumbs", service.getBreadcrumbs(actualId));
        model.addAttribute("nuevaCaracteristica", new Caracteristica());
        model.addAttribute("listaTodosPadres", service.getAllCaracteristicas());

        return "admin/caracteristicas";
    }

    @PostMapping("/admin/caracteristicas/agregar")
    public String agregarCaracteristicaAdmin(@ModelAttribute Caracteristica nuevaCaracteristica) {
        service.addCaracteristica(nuevaCaracteristica);
        return "redirect:/admin/caracteristicas";
    }


    @GetMapping("/oferente/habilidad")
    public String showHabilidadesOferente(
            @RequestParam(required = false) Integer actualId,
            Model model,
            Principal principal) {

        Usuario usuario = service.buscarUsuarioPorId(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", usuario != null ? usuario.getCorreo() : "");

        model.addAttribute("misHabilidades",
                service.obtenerHabilidadesDeOferente(principal.getName()));

        if (actualId == null) {
            model.addAttribute("subCategorias", service.getCaracteristicasRaiz());
            model.addAttribute("titulo", "Categorías: raíces");
            model.addAttribute("categoriaActual", null);
        } else {
            Caracteristica categoriaActual = service.getCaracteristica(actualId);
            model.addAttribute("subCategorias", service.getSubCaracteristicas(actualId));
            model.addAttribute("titulo", "Subcategorías de: "
                    + (categoriaActual != null ? categoriaActual.getNombre() : ""));
            model.addAttribute("categoriaActual", categoriaActual);
        }

        model.addAttribute("breadcrumbs", service.getBreadcrumbs(actualId));
        model.addAttribute("todasCategorias", service.getAllCaracteristicas());

        return "oferente/habilidades";
    }

    @PostMapping("/oferente/habilidad/agregar")
    public String agregarHabilidadOferente(
            @RequestParam Integer idCaracteristica,
            @RequestParam Integer nivel,
            @RequestParam(required = false) Integer actualId,
            Principal principal) {

        service.agregarHabilidadOferente(principal.getName(), idCaracteristica, nivel);

        if (actualId != null) {
            return "redirect:/oferente/habilidad?actualId=" + actualId;
        }
        return "redirect:/oferente/habilidad";
    }
}