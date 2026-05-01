package by.mcsaltine.vkpost.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${app.image.bucket:src/main/resources/employees/photo}")
    private String bucket;

    @SneakyThrows
    public void upload(String imageFilename, InputStream content) {
        if (StringUtils.isEmpty(imageFilename)) {
            return;
        }

        Path fullImagePath = Path.of(bucket, imageFilename);

        try (content) {
            Files.createDirectories(fullImagePath.getParent());
            Files.write(fullImagePath, content.readAllBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    @SneakyThrows
    public Optional<byte[]> get(String imagePath) {

        Path fullImageEmptyPath = Path.of(bucket, "no-photo.png");

        if (StringUtils.isEmpty(imagePath)) {
            return Optional.of(Files.readAllBytes(fullImageEmptyPath));
        }

        Path fullImagePath = Path.of(bucket, imagePath);

        return Files.exists(fullImagePath)
                ? Optional.of(Files.readAllBytes(fullImagePath))
                : Optional.of(Files.readAllBytes(fullImageEmptyPath));
    }

    @SneakyThrows
    public void delete(String imagePath) {
        Path fullImagePath = Path.of(bucket, imagePath);
        if(fullImagePath.toFile().exists()) {
            Files.delete(fullImagePath);
        }
    }
}