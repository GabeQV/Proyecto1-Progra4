package com.example.proyecto1.presentation.admin;

import com.example.proyecto1.logic.ReportePuesto;
import com.example.proyecto1.logic.Service;
import com.example.proyecto1.logic.Usuario;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {

    private final Service service;
    private final TemplateEngine templateEngine;

    // Spring inyecta ambos automáticamente
    public ReporteController(Service service, TemplateEngine templateEngine) {
        this.service = service;
        this.templateEngine = templateEngine;
    }

    // GET /admin/reportes — muestra el formulario
    @GetMapping
    public String mostrarFormulario(Model model, Principal principal) {
        Usuario admin = service.buscarUsuarioPorId(principal.getName()).orElse(null);
        model.addAttribute("adminEmail", admin != null ? admin.getCorreo() : "");
        model.addAttribute("meses", service.getNombresMeses());
        model.addAttribute("anioActual", LocalDate.now().getYear());
        return "admin/reportes";
    }

    // GET /admin/reportes/descargar?mes=3&anio=2026 — genera el PDF
    @GetMapping("/descargar")
    public void descargarPDF(
            @RequestParam int mes,
            @RequestParam int anio,
            Principal principal,
            HttpServletResponse response) throws Exception {

        // 1. Datos desde la VIEW — una sola línea
        List<ReportePuesto> puestos = service.getReportePorMes(mes, anio);

        Usuario admin = service.buscarUsuarioPorId(principal.getName()).orElse(null);

        // 2. Context es el Model manual para usar TemplateEngine directamente
        Context context = new Context();
        context.setVariable("puestos", puestos);
        context.setVariable("mes", mes);
        context.setVariable("anio", anio);
        context.setVariable("nombreMes", service.getNombresMeses().get(mes - 1));
        context.setVariable("adminEmail", admin != null ? admin.getCorreo() : "");

        // 3. Thymeleaf convierte el template a String HTML
        String html = templateEngine.process("admin/reporte-pdf", context);

        // 4. Flying Saucer convierte ese HTML a PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(baos);

        // 5. Se envía al navegador como descarga
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"reporte-" + mes + "-" + anio + ".pdf\"");
        response.getOutputStream().write(baos.toByteArray());
    }
}