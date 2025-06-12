package ProgressoApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Zrobione przekierowanie na stronę logowania, nie obsługuję dalej logowania na dany moment.
     */
}
