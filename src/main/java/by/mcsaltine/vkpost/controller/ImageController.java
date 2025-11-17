package by.mcsaltine.vkpost.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@RestController
@CrossOrigin(origins = "*")
public class ImageController implements WebMvcConfigurer {

    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/NewPhoto/**")
                .addResourceLocations("classpath:/NewPhoto/");
        registry.addResourceHandler("/OldPhoto/**")
                .addResourceLocations("classpath:/OldPhoto/");
    }

    @GetMapping("/api/images")
    public List<Map<String, String>> getImages() throws IOException {
        List<Map<String, String>> images1 = loadImagesFromFolder("NewPhoto", "Новые фото");
        List<Map<String, String>> images2 = loadImagesFromFolder("OldPhoto", "Старые фото");

        List<Map<String, String>> interleaved = new ArrayList<>();
        int max = Math.max(images1.size(), images2.size());
        for (int i = 0; i < max; i++) {
            if (i < images1.size()) interleaved.add(images1.get(i));
            if (i < images2.size()) interleaved.add(images2.get(i));
        }
        return interleaved;
    }

    private List<Map<String, String>> loadImagesFromFolder(String folder, String label) throws IOException {
        Resource resource = new ClassPathResource(folder);
        if (!resource.exists() || !resource.getFile().isDirectory()) {
            return Collections.emptyList();
        }

        return Files.list(resource.getFile().toPath())
                .filter(Files::isRegularFile)
                .filter(p -> IMAGE_EXTENSIONS.stream().anyMatch(ext -> p.toString().toLowerCase().endsWith(ext)))
                .sorted()
                .map(p -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("path", "/" + folder + "/" + p.getFileName().toString());
                    map.put("folder", label);
                    map.put("filename", p.getFileName().toString());
                    return map;
                })
                .collect(Collectors.toList());
    }
}