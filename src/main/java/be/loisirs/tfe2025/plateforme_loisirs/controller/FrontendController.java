package be.loisirs.tfe2025.plateforme_loisirs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping(value = {
            "/",
            "/activities",
            "/activities/{id}",
            "/shop",
            "/products/{id}",
            "/cart",
            "/login",
            "/register",
            "/partner",
            "/profile",

            "/admin",
            "/admin/users",
            "/admin/products",
            "/admin/products/new",
            "/admin/products/edit/{id}"


    })
    public String forward() {

        return "forward:/index.html";
    }
}