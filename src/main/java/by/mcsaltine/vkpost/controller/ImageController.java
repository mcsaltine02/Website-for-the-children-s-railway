package by.mcsaltine.vkpost.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class ImageController implements WebMvcConfigurer {

    private static final List<String> IMAGE_EXTENSIONS = List.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Без кэширования изображений
        registry.addResourceHandler("/NewPhoto/**")
                .addResourceLocations("classpath:/NewPhoto/")
                .setCachePeriod(0)
                .setCacheControl(CacheControl.noCache().mustRevalidate());

        registry.addResourceHandler("/OldPhoto/**")
                .addResourceLocations("classpath:/OldPhoto/")
                .setCachePeriod(0)
                .setCacheControl(CacheControl.noCache().mustRevalidate());
    }

    /**
     * Возвращает список всех фотографий (загружается заново при каждом запросе)
     */
    @GetMapping("/api/images")
    public List<Map<String, String>> getImages() {
        return loadAllImages();
    }

    /**
     * Принудительное обновление списка (на всякий случай)
     */
    @PostMapping("/api/images/refresh")
    public Map<String, String> refreshCache() {
        return Map.of(
                "status", "success",
                "message", "Список фотографий обновлён",
                "total", String.valueOf(loadAllImages().size())
        );
    }

    /**
     * Загрузка всех фотографий из папок
     */
    private List<Map<String, String>> loadAllImages() {
        List<Map<String, String>> images1 = loadImagesFromFolder("NewPhoto", "Новые фото");
        List<Map<String, String>> images2 = loadImagesFromFolder("OldPhoto", "Старые фото");

        // Чередование фотографий
        List<Map<String, String>> interleaved = new ArrayList<>();
        int max = Math.max(images1.size(), images2.size());

        for (int i = 0; i < max; i++) {
            if (i < images1.size()) interleaved.add(images1.get(i));
            if (i < images2.size()) interleaved.add(images2.get(i));
        }

        return interleaved;
    }

    private List<Map<String, String>> loadImagesFromFolder(String folderName, String label) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            Resource[] resources = resolver.getResources("classpath:/" + folderName + "/**/*.*");

            return Arrays.stream(resources)
                    .filter(Resource::isReadable)
                    .filter(resource -> {
                        String filename = resource.getFilename();
                        if (filename == null) return false;
                        String lowerName = filename.toLowerCase();
                        return IMAGE_EXTENSIONS.stream().anyMatch(lowerName::endsWith);
                    })
                    .sorted(Comparator.comparing(Resource::getFilename))
                    .map(resource -> {
                        Map<String, String> map = new HashMap<>();
                        String filename = resource.getFilename();
                        map.put("path", "/" + folderName + "/" + filename);
                        map.put("folder", label);
                        map.put("filename", filename);
                        return map;
                    })
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке папки " + folderName + ": " + e.getMessage());
            return List.of();
        }
    }
}