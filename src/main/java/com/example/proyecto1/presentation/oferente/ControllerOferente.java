package com.example.proyecto1.presentation.oferente;

import com.example.proyecto1.logic.Oferente;
import com.example.proyecto1.logic.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Controller
public class ControllerOferente {

    private final Service service;

    @Value("${cv.upload-dir:uploads/cvs}")
    private String uploadDir;

    public ControllerOferente(Service service) {
        this.service = service;
    }

    @GetMapping("/RegistroOferente")
    public String Ofer_register() {
        return "registro/regist-oferente";
    }

    @PostMapping("/SaveOfer")
    public String registrar(
            @RequestParam String id,
            @RequestParam String correo,
            @RequestParam String clave,
            @RequestParam String nombre,
            @RequestParam String primerApellido,
            @RequestParam String segundoApellido,
            @RequestParam String nacionalidad,
            @RequestParam String telefono,
            @RequestParam String residencia,
            RedirectAttributes redirectAttrs) {
        try {
            service.registrarOferente(id, correo, clave, nombre, primerApellido,
                    segundoApellido, nacionalidad, telefono, residencia);
            redirectAttrs.addFlashAttribute("exito",
                    "Registro exitoso. Tu cuenta está pendiente de aprobación.");
            return "redirect:/RegistroOferente";
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/RegistroOferente";
        }
    }

    @GetMapping("/oferente/dashboard")
    public String dashboard(Model model, Principal principal) {
        Oferente oferente = service.buscarPorIdOf(principal.getName());
        model.addAttribute("oferente", oferente);
        return "oferente/dashboard";
    }

    @GetMapping("/oferente/CV")
    public String verCV(Model model, Principal principal) {
        Oferente oferente = service.buscarPorIdOf(principal.getName());
        model.addAttribute("oferente", oferente);
        return "oferente/CV";
    }

    @PostMapping("/oferente/CV/subir")
    public String subirCV(
            @RequestParam("archivo") MultipartFile archivo,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        try {
            service.guardarCV(principal.getName(), archivo, uploadDir);
            redirectAttrs.addFlashAttribute("mensajeExito", "CV subido correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensajeError",
                    "Error al subir el archivo: " + e.getMessage());
        }
        return "redirect:/oferente/CV";
    }

    @GetMapping("/oferente/CV/descargar")
    public ResponseEntity<Resource> descargarCV(Principal principal) {
        try {
            Oferente oferente = service.buscarPorIdOf(principal.getName());

            if (oferente.getCvRuta() == null) {
                return ResponseEntity.notFound().build();
            }

            Path rutaArchivo = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize()
                    .resolve(oferente.getCvRuta());

            Resource resource = new UrlResource(rutaArchivo.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + oferente.getCvRuta() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/oferente/CV/eliminar")
    public String eliminarCV(Principal principal, RedirectAttributes redirectAttrs) {
        try {
            service.eliminarCV(principal.getName(), uploadDir);
            redirectAttrs.addFlashAttribute("mensajeExito", "CV eliminado correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensajeError",
                    "Error al eliminar el CV: " + e.getMessage());
        }
        return "redirect:/oferente/CV";
    }
}