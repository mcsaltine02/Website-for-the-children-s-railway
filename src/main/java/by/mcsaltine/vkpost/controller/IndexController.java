package by.mcsaltine.vkpost.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class IndexController {
    @GetMapping("/")
    public String index() {
        return "/index";
    }
    @GetMapping("/passengers")
    public String passengers() {
        return "/passengers";
    }

    @GetMapping("/photos")
    public String photos() {
        return "/photos";
    }
    @GetMapping("/test")
    public String test() {
        return "informationSecurity";
    }


}
