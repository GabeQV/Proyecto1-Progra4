package com.example.proyecto1.presentation.oferente;
import com.example.proyecto1.logic.service.RegistroService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@org.springframework.stereotype.Controller
public class ControllerOferente {
    private final RegistroService registroService;

    public ControllerOferente(RegistroService registroService){
        this.registroService=registroService;
    }
    @GetMapping ("/RegistroOferente")
        public String Ofer_register() {return "registro-oferente/regist-oferente";}

    @PostMapping("/SaveOfer")
    public String registrar(@RequestParam String id, @RequestParam String correo, @RequestParam String clave, @RequestParam String nombre, @RequestParam String primerApellido, @RequestParam String segundoApellido, @RequestParam String nacionalidad, @RequestParam String telefono, @RequestParam String residencia, RedirectAttributes redirectAttrs
    ) {
        try {
            registroService.registrarOferente(id, correo, clave, nombre, primerApellido, segundoApellido, nacionalidad, telefono, residencia
            );
            redirectAttrs.addFlashAttribute("exito", "Registro exitoso. Tu cuenta está pendiente de aprobación.");

            return "redirect:/RegistroOferente";

        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/RegistroOferente";
        }
    }
}
