package ru.oshokin.controllers.gui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public String getLoginForm() {
        return "login_form";
    }

}