package com.example.proyecto1.presentation.main_view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class controller_main_view {
    @GetMapping("/")
    public String main_view() {
        return "main_view/View";
    }
}
