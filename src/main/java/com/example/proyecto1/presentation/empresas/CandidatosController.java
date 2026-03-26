package com.example.proyecto1.presentation.empresas;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.Oferente;
import com.example.proyecto1.logic.OferenteHabilidad;
import com.example.proyecto1.logic.Puesto;
import com.example.proyecto1.logic.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Path;
import java.security.Principal;
import java.util.List;

@Controller
public class CandidatosController {

    private final Service service;

    public CandidatosController(Service service) {
        this.service = service;
    }

    @GetMapping("/empresa/candidatos")
    public String buscarCandidatos(
            @RequestParam Integer puestoId,
            Model model,
            Principal principal) {

        Empresa empresa = service.buscarPorIdEmp(principal.getName());
        Puesto puesto   = service.getPuesto(puestoId);

        List<Service.ResultadoCandidato> resultados = service.buscarCandidatos(puestoId);

        model.addAttribute("empresa",    empresa);
        model.addAttribute("puesto",     puesto);
        model.addAttribute("resultados", resultados);
        return "empresas/candidatos";
    }

    @GetMapping("/empresa/candidatos/{idOferente}")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String verDetalleCandidato(
            @PathVariable String idOferente,
            @RequestParam Integer puestoId,
            Model model,
            Principal principal) {

        Empresa empresa   = service.buscarPorIdEmp(principal.getName());
        Oferente oferente = service.buscarPorIdOf(idOferente);
        List<OferenteHabilidad> habilidades =
                service.obtenerHabilidadesDeOferente(idOferente);

        model.addAttribute("empresa",     empresa);
        model.addAttribute("oferente",    oferente);
        model.addAttribute("habilidades", habilidades);
        model.addAttribute("puestoId",    puestoId);
        return "empresas/detalle-candidato";
    }

    @GetMapping("/empresa/candidatos/cv/{idOferente}")
    public ResponseEntity<Resource> descargarCVCandidato(
            @PathVariable String idOferente) {
        try {
            Oferente oferente = service.buscarPorIdOf(idOferente);
            if (oferente.getCvRuta() == null) {
                return ResponseEntity.notFound().build();
            }
            Path rutaArchivo = java.nio.file.Paths.get("uploads/cvs")
                    .toAbsolutePath().normalize()
                    .resolve(oferente.getCvRuta());
            org.springframework.core.io.Resource resource =
                    new org.springframework.core.io.UrlResource(rutaArchivo.toUri());
            if (!resource.exists()) return ResponseEntity.notFound().build();

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + oferente.getCvRuta() + "\"")
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}