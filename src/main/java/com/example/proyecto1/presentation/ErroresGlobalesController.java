package com.example.proyecto1.presentation;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class ErroresGlobalesController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        boolean esErrorDeTamano = false;

        if (status != null && Integer.parseInt(status.toString()) == 413) {
            esErrorDeTamano = true;
        } else if (exception != null && exception.toString().contains("SizeLimitExceededException")) {
            esErrorDeTamano = true;
        }

        if (esErrorDeTamano) {
            FlashMap flashMap = new FlashMap();
            flashMap.put("mensajeError", "Error: El archivo es demasiado pesado. El límite máximo es de 3 MB.");

            FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
            if (flashMapManager != null) {
                flashMapManager.saveOutputFlashMap(flashMap, request, response);
            }

            return "redirect:/oferente/CV";
        }

        return "redirect:/";
    }
}