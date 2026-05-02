package by.mcsaltine.vkpost.controller;

import by.mcsaltine.vkpost.model.ScheduleLesson;
import by.mcsaltine.vkpost.parser.RdjdScheduleParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ScheduleController {

    private static List<ScheduleLesson> cachedLessons = new ArrayList<>();

    private Path filePath = Paths.get("/data/excel/schedule/schedule.xlsx");

    public ScheduleController() {
        this.filePath = Paths.get("/data/excel/schedule/schedule.xlsx");
    }

    @PostConstruct
    public void init() {
        createDirectoriesIfNeeded();
        loadFromFileIfExists();
    }

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("lessons", cachedLessons);
        model.addAttribute("days", List.of("Воскресенье", "Понедельник", "Вторник", "Среда",
                "Четверг", "Пятница", "Суббота"));
        return "students";
    }

    @PostMapping("/raspisanie/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Файл пустой");
            return "redirect:/students";
        }

        try {
            Files.createDirectories(filePath.getParent());

            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            loadFromFileIfExists();

            redirectAttributes.addFlashAttribute("message", "Расписание успешно обновлено!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Ошибка при сохранении файла: " + e.getMessage());
        }

        return "redirect:/students";
    }

    private void createDirectoriesIfNeeded() {
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            System.err.println("Не удалось создать директории: " + e.getMessage());
        }
    }

    private void loadFromFileIfExists() {
        try {
            if (Files.exists(filePath)) {
                try (var is = Files.newInputStream(filePath)) {
                    cachedLessons = RdjdScheduleParser.parse(is);
                }
            } else {
                try (var is = getClass().getClassLoader()
                        .getResourceAsStream("/data/excel/schedule/schedule.xlsx")) {
                    if (is != null) {
                        cachedLessons = RdjdScheduleParser.parse(is);
                    } else {
                        cachedLessons = new ArrayList<>();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка при загрузке расписания: " + e.getMessage());
            cachedLessons = new ArrayList<>();
        }
    }
}