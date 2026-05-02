package by.mcsaltine.vkpost.controller.employees;

import by.mcsaltine.vkpost.controller.employees.payload.CreateTeacherDTO;
import by.mcsaltine.vkpost.model.Employee;
import by.mcsaltine.vkpost.repository.*;
import by.mcsaltine.vkpost.service.EmployeeService;
import by.mcsaltine.vkpost.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeachersController {

    private final EmployeeService employeeService;
    private final ImageService imageService;
    private final EmployeeRepository employeeRepository;
    private final AcademicDegreeRepository academicDegreeRepository;
    private final PostRepository postRepository;
    private final AttractionConditionRepository attractionConditionRepository;
    private final TaughtProgramRepository taughtProgramRepository;

    @GetMapping
    public String allTeachers(Model model) {
        model.addAttribute("employee", employeeRepository.findAll());   // лучше назвать "employees"
        return "main_info_about_organization/teachers/teachers";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createTeacher", new CreateTeacherDTO());

        model.addAttribute("academicDegrees", academicDegreeRepository.findAll());
        model.addAttribute("posts", postRepository.findAll());
        model.addAttribute("attractionConditions", attractionConditionRepository.findAll());
        model.addAttribute("taughtPrograms", taughtProgramRepository.findAll());

        return "main_info_about_organization/teachers/addTeacher";
    }

    @PostMapping("/create")
    public String createTeacher(
            @ModelAttribute("createTeacher") CreateTeacherDTO dto,
            RedirectAttributes redirectAttributes) {
        System.out.println(dto.toString());

        try {
            Employee savedEmployee = employeeService.createTeacher(dto, dto.getNewPhoto());
            return "redirect:/teachers/" + savedEmployee.getEId();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании: " + e.getMessage());
            return "redirect:/teachers/create";
        }
    }

    @PostMapping("/createNoSuchPhoto")
    public String createNoSuchPhoto(@RequestParam("photo") MultipartFile newPhoto) throws IOException {

        if (newPhoto.isEmpty()) {
            return "redirect:/teachers?error=empty";
        }

        // Опционально: проверка расширения
        String filename = newPhoto.getOriginalFilename();
        if (filename == null ||
                !(filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg") || filename.endsWith(".webp"))) {
            return "redirect:/teachers?error=invalid_format";
        }

        this.imageService.upload("no-photo.png", newPhoto.getInputStream());

        return "redirect:/teachers?success";
    }
}
