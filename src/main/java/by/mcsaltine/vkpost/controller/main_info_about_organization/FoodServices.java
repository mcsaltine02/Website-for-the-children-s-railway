package by.mcsaltine.vkpost.controller.main_info_about_organization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Controller
@RequestMapping("/main-info-about-organization/food")
public class FoodServices {

    @Value("${app.upload.food.dir:./uploads/food-services}")
    private String uploadDir;

    @Value("${app.number-phone}")
    private String numberPhone;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @GetMapping
    public String foodServices(Model model) {
        addCurrentFileToModel(model);
        model.addAttribute("numberPhone", numberPhone);
        return "main_info_about_organization/food-services";
    }

    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) throws IOException {

        if (file.isEmpty()) {
            model.addAttribute("error", "Файл не выбран");
            addCurrentFileToModel(model);
            return "main_info_about_organization/food-services";
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
            model.addAttribute("error", "Разрешены только файлы .xlsx");
            addCurrentFileToModel(model);
            return "main_info_about_organization/food-services";
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadPath)) {
            for (Path existing : stream) {
                if (Files.isRegularFile(existing)) {
                    Files.delete(existing);
                }
            }
        }

        String today = LocalDate.now().format(DATE_FORMAT);
        String newFilename = today + ".sm.xlsx";

        Path newFilePath = uploadPath.resolve(newFilename);
        file.transferTo(newFilePath);
        model.addAttribute("message", "Меню успешно обновлено на " + today);
        addCurrentFileToModel(model);

        return "main_info_about_organization/food-services";
    }

    private void addCurrentFileToModel(Model model) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadPath)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String filename = file.getFileName().toString();
                    String downloadUrl = "/food-services/" + filename;

                    model.addAttribute("filename", filename.replace(".xlsx", ""));
                    model.addAttribute("downloadUrl", downloadUrl);
                    return;
                }
            }
        } catch (IOException e) {}

        model.addAttribute("filename", null);
    }
}