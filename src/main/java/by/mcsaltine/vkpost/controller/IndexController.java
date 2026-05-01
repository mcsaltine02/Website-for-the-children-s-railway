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

    @GetMapping("/basicInformation")
    public String basicInformation() {
        return "main_info_about_organization/basicInformation";
    }

    @GetMapping("/logisticalSupport")
    public String logisticalSupport() {
        return "main_info_about_organization/logisticalSupport";
    }

    @GetMapping("/main-info-about-organization/financial-activities")
    public String financialActivities() {
        return "main_info_about_organization/financial-activities";
    }

    @GetMapping("/main-info-about-organization/available-environment")
    public String availableEnvironment() {
        return "main_info_about_organization/available-environment";
    }

    @GetMapping("/main-info-about-organization/management-bodies")
    public String managementBodies() {
        return "main_info_about_organization/management-bodies";
    }
    @GetMapping("/main-info-about-organization/education")
    public String education() {
        return "main_info_about_organization/education";
    }



    @GetMapping("/login")
    public String login() {
        return "/login";
    }
}
