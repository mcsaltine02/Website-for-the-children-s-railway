package by.mcsaltine.vkpost.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PdfCategoriesService {
    public class Category {
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

    private final List<Category> categories;

    public PdfCategoriesService() {
        this.categories = Arrays.asList(
                new Category("Обучающимся", "Obuchayuschimsya", "obuchayuschimsya"),
                new Category("Педагогам", "Pedagogam", "pedagogam"),
                new Category("Родителям", "Roditelyam", "roditelyam"),
                new Category("Локальные акты", "LokalnyeAkty", "lokalnye-akty"),
                new Category("Нормативное регулирование", "NormativnoeRegulirovanie", "normativnoe-regulirovanie"),
                new Category("Детские безопасные сайты", "DetskieBezopasnyeSajty", "detskie-bezopasnye-sajty")
        );
    }

    @PostConstruct
    public void loadAllFiles() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        for (Category cat : categories) {
            String pattern = "classpath:static/pdfs/" + cat.folder + "/*.pdf";

            try {
                Resource[] resources = resolver.getResources(pattern);

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
                        .collect(Collectors.toList());

                cat.files = files;   // заполняем один раз

            } catch (IOException e) {
                cat.files = Collections.emptyList();
            }
        }
    }

    public List<Category> getAllCategories() {
        return categories; // возвращаем тот же объект (immutable по смыслу)
    }
}
