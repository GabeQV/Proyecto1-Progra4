package com.example.proyecto1.presentation.main_view;

import com.example.proyecto1.logic.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class controller_main_view {

    private final Service service;

    public controller_main_view(Service service) {
        this.service = service;
    }

    @GetMapping("/")
    public String paginaPrincipal(Model model) {
        model.addAttribute("puestosRecientes",
                service.getTop5PuestosPublicos());
        return "main_view/View";
    }

    @GetMapping("/buscar-puestos")
    public String buscarPuestos(
            @RequestParam(required = false) List<Integer> ids,
            Model model) {

        model.addAttribute("caracteristicas",
                service.getCaracteristicasRaiz());

        if (ids != null && !ids.isEmpty()) {
            model.addAttribute("resultados",
                    service.buscarPuestosPublicos(ids));
        }

        return "main_view/buscar-puestos";
    }
}

//    @GetMapping("/buscar-puestos")
//    public String buscarPuestos(
//            @RequestParam(required = false) List<Integer> idCaracteristicas,
//            Model model) {
//        model.addAttribute("caracteristicas", service.getCaracteristicasRaiz());
//        if (idCaracteristicas != null && !idCaracteristicas.isEmpty()) {
//            model.addAttribute("resultados",
//                    service.buscarPuestosPublicosPorCaracteristicas(idCaracteristicas));
//        }
//        return "main_view/buscar-puestos"; // nueva vista
//    }

