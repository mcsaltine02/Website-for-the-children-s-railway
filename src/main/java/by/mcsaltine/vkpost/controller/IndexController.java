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
    @GetMapping("/basicInformation")
    public String basicInformation() {
        return "main_info_about_organization/basicInformation";
    }
    @GetMapping("/logisticalSupport")
    public String logisticalSupport() {
        return "main_info_about_organization/logisticalSupport";
    }
    @GetMapping("/allTeachers")
    public String allTeachers() {
        return "main_info_about_organization/teachers/allTeachers";
    }


}
