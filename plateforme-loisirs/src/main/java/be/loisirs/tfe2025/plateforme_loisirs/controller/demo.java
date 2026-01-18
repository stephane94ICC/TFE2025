package be.loisirs.tfe2025.plateforme_loisirs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class demo {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, world!";
    }
}