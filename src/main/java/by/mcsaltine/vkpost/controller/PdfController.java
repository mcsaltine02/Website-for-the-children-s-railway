package by.mcsaltine.vkpost.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.*;

@Controller
public class PdfController {

    public static class Category {
        String name;
        String folder;
        String fragmentName;
        List<String> files = new ArrayList<>();

        public Category(String name, String folder, String fragmentName) {
            this.name = name;
            this.folder = folder;
            this.fragmentName = fragmentName;
        }

        public String getName() { return name; }
        public String getFolder() { return folder; }
        public String getFragmentName() { return fragmentName; }
        public List<String> getFiles() { return files; }
        public boolean hasFiles() { return !files.isEmpty(); }
    }

    private static final List<Category> ALL_CATEGORIES = Arrays.asList(
            new Category("Обучающимся", "Obuchayuschimsya", "obuchayuschimsya"),
            new Category("Педагогам", "Pedagogam", "pedagogam"),
            new Category("Родителям", "Roditelyam", "roditelyam"),
            new Category("Локальные акты", "LokalnyeAkty", "lokalnye-akty"),
            new Category("Нормативное регулирование", "NormativnoeRegulirovanie", "normativnoe-regulirovanie"),
            new Category("Детские безопасные сайты", "DetskieBezopasnyeSajty", "detskie-bezopasnye-sajty")
    );

    @GetMapping("/informationSecurity")
    public String showCategorizedPdfs(Model model) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        for (Category cat : ALL_CATEGORIES) {
            String pattern = "classpath:static/pdfs/" + cat.folder + "/*.pdf";

            Resource[] resources;
            try {
                resources = resolver.getResources(pattern);
            } catch (IOException e) {
                // Папки нет — просто пустой массив
                resources = new Resource[0];
            }

            List<String> files = Arrays.stream(resources)
                    .map(r -> {
                        try {
                            return r.getFilename();
                        } catch (Exception ex) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList();

            cat.files = files;
        }

        model.addAttribute("categories", ALL_CATEGORIES);
        return "informationSecurity";
    }

}