package by.mcsaltine.vkpost.controller.employees;

import by.mcsaltine.vkpost.controller.employees.payload.UpdateTeacherDTO;
import by.mcsaltine.vkpost.model.Employee;
import by.mcsaltine.vkpost.repository.*;
import by.mcsaltine.vkpost.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.NoSuchElementException;


@Controller
@RequiredArgsConstructor
@RequestMapping("/teachers/{teacherId:\\d+}")
public class TeacherController {

    private final EmployeeService employeeService;
    private final AcademicDegreeRepository academicDegreeRepository;
    private final PostRepository postRepository;
    private final AttractionConditionRepository attractionConditionRepository;
    private final TaughtProgramRepository taughtProgramRepository;

    @ModelAttribute("employee")
    public Employee employee(@PathVariable("teacherId") Integer teacherId) {
        return employeeService.findById(teacherId);
    }

    @GetMapping
    public String teacher() {
        return "main_info_about_organization/teachers/teacher";
    }

    // ====================== РЕДАКТИРОВАНИЕ ======================
    @GetMapping("/edit")
    public String showEditForm(@PathVariable("teacherId") Integer teacherId, Model model) {
        Employee employee = employeeService.findById(teacherId);
        UpdateTeacherDTO dto = employeeService.convertToUpdateDTO(employee);

        model.addAttribute("updateTeacher", dto);
        model.addAttribute("academicDegrees", academicDegreeRepository.findAll());
        model.addAttribute("posts", postRepository.findAll());
        model.addAttribute("attractionConditions", attractionConditionRepository.findAll());
        model.addAttribute("taughtPrograms", taughtProgramRepository.findAll());

        return "main_info_about_organization/teachers/editTeacher";
    }

    @PostMapping("/edit")
    public String updateTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @ModelAttribute("updateTeacher") UpdateTeacherDTO dto,
            RedirectAttributes redirectAttributes) {

        try {
            employeeService.updateTeacher(dto);
            return "redirect:/teachers/" + teacherId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении: " + e.getMessage());
            return "redirect:/teachers/" + teacherId + "/edit";
        }
    }

    @PostMapping("/delete")
    public String deleteTeacher(@PathVariable("teacherId") Integer teacherId,
                                RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(teacherId);
            redirectAttributes.addFlashAttribute("success", "Сотрудник успешно удалён.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/teachers";
    }

    // ====================== ПРОСМОТР ФОТО ======================
    @GetMapping("/photo")
    public ResponseEntity<byte[]> getEmployeePhoto(@PathVariable Integer teacherId) {
        Employee employee = employeeService.findById(teacherId);
        return employeeService.findPhoto(employee)
                .map(content -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                        .body(content))
                .orElse(ResponseEntity.notFound().build());
    }
}
