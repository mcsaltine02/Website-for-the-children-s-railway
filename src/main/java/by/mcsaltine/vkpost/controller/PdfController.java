package by.mcsaltine.vkpost.controller;

import by.mcsaltine.vkpost.service.PdfCategoriesService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
@Controller
public class PdfController {

    private final PdfCategoriesService pdfCategoriesService;

    public PdfController(PdfCategoriesService pdfCategoriesService) {
        this.pdfCategoriesService = pdfCategoriesService;
    }

    @GetMapping("/informationSecurity")
    public String showCategorizedPdfs(Model model) {
        model.addAttribute("categories", pdfCategoriesService.getAllCategories());
        return "informationSecurity";
    }
}