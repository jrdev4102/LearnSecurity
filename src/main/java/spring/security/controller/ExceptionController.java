package spring.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ExceptionController {

    @GetMapping("/denied")
    public String accessDenied(@RequestParam(value = "exception", required = false) String exception,
                               Model model) {
        model.addAttribute("message", "Access Denied");
        return "/exception/exception";
    }

}
