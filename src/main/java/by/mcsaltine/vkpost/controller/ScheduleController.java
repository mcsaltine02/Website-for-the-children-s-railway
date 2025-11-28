// src/main/java/by/mcsaltine/vkpost/controller/ScheduleController.java
package by.mcsaltine.vkpost.controller;

import by.mcsaltine.vkpost.model.ScheduleLesson;
import by.mcsaltine.vkpost.parser.RdjdScheduleParser;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
@Controller
public class ScheduleController {

    // Кэшируем здесь — живёт, пока приложение запущено
    private static List<ScheduleLesson> cachedLessons = new ArrayList<>();
    private final Path filePath = Path.of("schedule.xlsx");

    // Загружаем при старте приложения + при каждой загрузке файла
    @PostConstruct
    public void init() {
        loadFromFileIfExists();
    }

    @GetMapping("/students.html")
    public String students(Model model) {
        model.addAttribute("lessons", cachedLessons); // ← мгновенно из памяти
        model.addAttribute("days", List.of("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"));
        return "students";
    }

    @PostMapping("/raspisanie/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         RedirectAttributes redirectAttributes) {
        try {
            Files.write(filePath, file.getBytes());
            loadFromFileIfExists(); // ← обновляем кэш
            redirectAttributes.addFlashAttribute("message", "Расписание успешно обновлено!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/students.html";
    }

    // Один раз парсим файл — и всё
    private void loadFromFileIfExists() {
        try (var is = Files.exists(filePath)
                ? Files.newInputStream(filePath)
                : getClass().getClassLoader().getResourceAsStream("schedule.xlsx")) {

            if (is != null) {
                cachedLessons = RdjdScheduleParser.parse(is);
            } else {
                cachedLessons = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить расписание при старте: " + e.getMessage());
            cachedLessons = new ArrayList<>();
        }
    }
}