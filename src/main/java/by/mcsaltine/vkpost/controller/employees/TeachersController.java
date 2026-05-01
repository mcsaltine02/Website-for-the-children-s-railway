package by.mcsaltine.vkpost.controller.employees;

import by.mcsaltine.vkpost.controller.employees.payload.CreateTeacherDTO;
import by.mcsaltine.vkpost.model.Employee;
import by.mcsaltine.vkpost.repository.*;
import by.mcsaltine.vkpost.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeachersController {

    private final EmployeeService employeeService;
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
        try {
            Employee savedEmployee = employeeService.createTeacher(dto, dto.getNewPhoto());
            return "redirect:/teachers/" + savedEmployee.getEId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании: " + e.getMessage());
            return "redirect:/teachers/create";
        }
    }
}
