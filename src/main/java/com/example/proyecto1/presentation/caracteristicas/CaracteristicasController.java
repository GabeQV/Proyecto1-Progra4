package com.example.proyecto1.presentation.caracteristicas;

import com.example.proyecto1.data.CaracteristicaRepository;
import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.logic.Caracteristica;
import com.example.proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class CaracteristicasController {

    @Autowired
    private CaracteristicaRepository caracteristicaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/admin/caracteristicas")
    public String showCaracteristicas(@RequestParam(required = false) Integer actualId, Model model, Principal principal) {

        Usuario admin = usuarioRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "No encontrado");

        List<Caracteristica> subCategorias;
        List<Caracteristica> breadcrumbs = new ArrayList<>();

        if (actualId == null) {
            subCategorias = caracteristicaRepository.findByIdPadreIsNull();
            model.addAttribute("titulo", "Categorías: raíces");
        } else {
            subCategorias = caracteristicaRepository.findByIdPadre_Id(actualId);

            Caracteristica actual = caracteristicaRepository.findById(actualId).orElse(null);
            model.addAttribute("titulo", "Subcategorías de: " + (actual != null ? actual.getNombre() : ""));
            while (actual != null) {
                breadcrumbs.add(actual);
                actual = actual.getIdPadre();
            }
            Collections.reverse(breadcrumbs);
        }

        model.addAttribute("subCategorias", subCategorias);
        model.addAttribute("breadcrumbs", breadcrumbs);

        // Para el formulario de "Agregar Característica"
        model.addAttribute("nuevaCaracteristica", new Caracteristica());
        model.addAttribute("listaTodosPadres", caracteristicaRepository.findAll());

        return "admin/caracteristicas";
    }

    @PostMapping("/admin/caracteristicas/agregar")
    public String agregarCaracteristica(@ModelAttribute Caracteristica nuevaCaracteristica) {
        if (nuevaCaracteristica.getIdPadre() != null && nuevaCaracteristica.getIdPadre().getId() == 0) {
            nuevaCaracteristica.setIdPadre(null);
        }
        caracteristicaRepository.save(nuevaCaracteristica);
        return "redirect:/admin/caracteristicas";
    }
}