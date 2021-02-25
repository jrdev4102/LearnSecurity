package spring.security.controller;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.security.domain.Admin;
import spring.security.domain.dto.AdminDto;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/user")
    public String user() {
        return "index";
    }

    @PostMapping("/user")
    public String createUser(AdminDto adminDto){

        ModelMapper modelMapper = new ModelMapper();
        Admin admin = modelMapper.map(adminDto, Admin.class);

        return "redirect:/";
    }

    @GetMapping("/admin")
    public String admin() {
        return "index";
    }

    @GetMapping("/sys")
    public String sys() {
        return "index";
    }

}
