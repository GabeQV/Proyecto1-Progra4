package com.example.proyecto1.presentation.caracteristicas;

import com.example.proyecto1.data.CaracteristicaRepository;
import com.example.proyecto1.data.OferenteHabilidadRepository;
import com.example.proyecto1.data.OferenteRepository;
import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.logic.*;
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
import java.util.Map;

@Controller
public class CaracteristicasController {

    private final Service service;
    private final UsuarioRepository usuarioRepository;
    private final OferenteHabilidadRepository oferenteHabilidadRepository;
    private final OferenteRepository oferenteRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    @Autowired
    public CaracteristicasController(Service service, UsuarioRepository usuarioRepository, OferenteHabilidadRepository oferenteHabilidadRepository, OferenteRepository oferenteRepository, CaracteristicaRepository caracteristicaRepository) {
        this.service = service;
        this.usuarioRepository = usuarioRepository;
        this.oferenteHabilidadRepository = oferenteHabilidadRepository;
        this.oferenteRepository = oferenteRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }


    // ── ADMIN ──────────────────────────────────────────────────────────────

    @GetMapping("/admin/caracteristicas")
    public String showCaracteristicasAdmin(
            @RequestParam(required = false) Integer actualId,
            Model model,
            Principal principal) {

        Usuario admin = usuarioRepository.findById(principal.getName()).orElse(null);
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
        // La lógica de presentación (manejar el "0" del select) se queda en el controlador
        if (nuevaCaracteristica.getIdPadre() != null && nuevaCaracteristica.getIdPadre().getId() == null) {
            nuevaCaracteristica.setIdPadre(null);
        } else if (nuevaCaracteristica.getIdPadre() != null && nuevaCaracteristica.getIdPadre().getId() == 0) {
            nuevaCaracteristica.setIdPadre(null);
        }

        service.addCaracteristica(nuevaCaracteristica);
        return "redirect:/admin/caracteristicas";
    }

    // ── OFERENTE ───────────────────────────────────────────────────────────

    @GetMapping("/oferente/habilidad")
    public String showHabilidadesOferente(
            @RequestParam(required = false) Integer actualId, Model model, Principal principal) {

        Usuario usuario = usuarioRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", usuario != null ? usuario.getCorreo() : "");

        Oferente oferente = oferenteRepository.findById(principal.getName()).orElse(null);
        List<OferenteHabilidad> misHabilidades = new ArrayList<>();
        if (oferente != null) {
            oferenteHabilidadRepository
                    .findAll()
                    .forEach(h -> {
                        if (((OferenteHabilidad) h).getIdOferente().getId().equals(principal.getName())) {
                            misHabilidades.add((OferenteHabilidad) h);
                        }
                    });
        }
        model.addAttribute("misHabilidades", misHabilidades);

        // ── Panel central: árbol de categorías navegable ──
        List<Caracteristica> subCategorias;
        List<Caracteristica> breadcrumbs = new ArrayList<>();
        Caracteristica categoriaActual = null;

        if (actualId == null) {
            subCategorias = caracteristicaRepository.findByIdPadreIsNull();
            model.addAttribute("titulo", "Categorías: raíces");
        } else {
            categoriaActual = caracteristicaRepository.findById(actualId).orElse(null);
            subCategorias = caracteristicaRepository.findByIdPadre_Id(actualId);
            model.addAttribute("titulo", "Subcategorías de: " + (categoriaActual != null ? categoriaActual.getNombre() : ""));

            Caracteristica cursor = categoriaActual;
            while (cursor != null) {
                breadcrumbs.add(cursor);
                cursor = cursor.getIdPadre();
            }
            Collections.reverse(breadcrumbs);
        }

        model.addAttribute("subCategorias", subCategorias);
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("categoriaActual", categoriaActual);

        // ── Panel derecho: select con las categorías disponibles ──
        model.addAttribute("todasCategorias", caracteristicaRepository.findAll());

        return "oferente/habilidades";
    }

    @PostMapping("/oferente/habilidad/agregar")
    public String agregarHabilidadOferente(
            @RequestParam Integer idCaracteristica,
            @RequestParam Integer nivel,
            @RequestParam(required = false) Integer actualId,
            Principal principal) {

        Oferente oferente = oferenteRepository.findById(principal.getName()).orElse(null);
        Caracteristica caracteristica = caracteristicaRepository.findById(idCaracteristica).orElse(null);

        if (oferente != null && caracteristica != null) {
            OferenteHabilidadId compositeId = new OferenteHabilidadId();
            compositeId.setIdOferente(principal.getName());
            compositeId.setIdCaracteristica(idCaracteristica);

            OferenteHabilidad habilidad = new OferenteHabilidad();
            habilidad.setId(compositeId);
            habilidad.setIdOferente(oferente);
            habilidad.setIdCaracteristica(caracteristica);
            habilidad.setNivel(nivel);

            oferenteHabilidadRepository.save(habilidad);
        }

        // Vuelve al mismo nivel del árbol donde estaba
        if (actualId != null) {
            return "redirect:/oferente/habilidad?actualId=" + actualId;
        }
        return "redirect:/oferente/habilidad";
    }
}