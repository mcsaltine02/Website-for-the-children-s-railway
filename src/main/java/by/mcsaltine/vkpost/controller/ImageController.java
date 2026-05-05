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

import javax.annotation.PostConstruct;
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

    // Кэш списка фотографий (загружается один раз)
    private volatile List<Map<String, String>> cachedImages = null;

    @PostConstruct
    public void init() {
        // Загружаем фотографии при старте приложения
        this.cachedImages = loadAllImages();
        System.out.println("Фотографии успешно загружены в кэш для странички 'Фотогаллерея'. Всего: " + cachedImages.size() + " шт.");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Сильное кэширование статических ресурсов (фотографии)
        registry.addResourceHandler("/NewPhoto/**")
                .addResourceLocations("classpath:/NewPhoto/")
                .setCachePeriod(86400)   // 24 часа
                .setCacheControl(CacheControl.maxAge(24, TimeUnit.HOURS).cachePublic());

        registry.addResourceHandler("/OldPhoto/**")
                .addResourceLocations("classpath:/OldPhoto/")
                .setCachePeriod(86400)
                .setCacheControl(CacheControl.maxAge(24, TimeUnit.HOURS).cachePublic());
    }

    /**
     * Основной эндпоинт — очень быстрый, возвращает кэш
     */
    @GetMapping("/api/images")
    public List<Map<String, String>> getImages() {
        return cachedImages != null ? cachedImages : List.of();
    }

    /**
     * Принудительное обновление кэша (на всякий случай)
     */
    @PostMapping("/api/images/refresh")
    public Map<String, String> refreshCache() {
        this.cachedImages = loadAllImages();
        return Map.of(
                "status", "success",
                "message", "Кэш обновлён",
                "total", String.valueOf(cachedImages.size())
        );
    }

    /**
     * Загрузка всех фотографий
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