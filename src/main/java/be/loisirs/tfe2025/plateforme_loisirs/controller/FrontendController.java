package be.loisirs.tfe2025.plateforme_loisirs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping(value = {
            "/",
            "/users",
            "/activities",
            "/activities/{id}",
            "/shop",
            "/products/{id}",
            "/cart",
            "/login",
            "/register",
            "/partner",
            "/admin",
            "/admin/products",
            "/profile"
    })
    public String forward() {
        return "forward:/index.html";
    }
}