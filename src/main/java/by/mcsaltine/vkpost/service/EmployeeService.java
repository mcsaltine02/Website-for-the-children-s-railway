package by.mcsaltine.vkpost.service;

import by.mcsaltine.vkpost.controller.employees.payload.CreateTeacherDTO;
import by.mcsaltine.vkpost.controller.employees.payload.UpdateTeacherDTO;
import by.mcsaltine.vkpost.model.*;
import by.mcsaltine.vkpost.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AcademicDegreeRepository academicDegreeRepository;
    private final PostRepository postRepository;
    private final AttractionConditionRepository attractionConditionRepository;
    private final EducationLevelRepository educationLevelRepository;
    private final TaughtProgramRepository taughtProgramRepository;
    private final ImageService imageService;


    // ====================== СОЗДАНИЕ ======================
    @Transactional
    public Employee createTeacher(CreateTeacherDTO dto, MultipartFile photo) throws IOException {
        Employee employee = new Employee();

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setMiddleName(dto.getMiddleName());
        employee.setWorkExperience(dto.getWorkExperience());
        employee.setWorkExperienceInEducationalInstitution(dto.getWorkExperienceInEducationalInstitution());
        if (!photo.isEmpty()) {
            employee.setPhoto(photo.getOriginalFilename());
            imageService.upload(photo.getOriginalFilename(), photo.getInputStream());
        }else{
            employee.setPhoto("no-photo.png");
        }

        handleAcademicDegree(employee, dto.getAcademicDegreeId(), dto.getNewAcademicDegree());
        handlePost(employee, dto.getPostId(), dto.getNewPost());
        handleAttractionCondition(employee, dto.getAttractionConditionId(), dto.getNewAttractionCondition());

        // Сохраняем сотрудника, чтобы получить ID
        Employee savedEmployee = employeeRepository.save(employee);

        // Добавляем связи
        addEducationLevels(savedEmployee, dto.getNewEducationLevels());
        addProfessionalDevelopments(savedEmployee, dto.getProfessionalDevelopments());
        addProfessionalRetraining(savedEmployee, dto.getProfessionalRetraining());
        addTaughtPrograms(savedEmployee, dto.getTaughtProgramIds(), dto.getNewTaughtPrograms());

        return employeeRepository.save(savedEmployee);
    }

    // ====================== ОБНОВЛЕНИЕ ======================
    @Transactional
    public Employee updateTeacher(UpdateTeacherDTO dto) throws IOException {
        Employee employee = findById(dto.getEId());
        MultipartFile photo = dto.getNewPhoto();

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setMiddleName(dto.getMiddleName());
        employee.setWorkExperience(dto.getWorkExperience());
        employee.setWorkExperienceInEducationalInstitution(dto.getWorkExperienceInEducationalInstitution());
        if (!photo.isEmpty()) {
            imageService.delete(employee.getPhoto());
            employee.setPhoto(photo.getOriginalFilename());
            imageService.upload(photo.getOriginalFilename(), photo.getInputStream());
        }

        handleAcademicDegree(employee, dto.getAcademicDegreeId(), dto.getNewAcademicDegree());
        handlePost(employee, dto.getPostId(), dto.getNewPost());
        handleAttractionCondition(employee, dto.getAttractionConditionId(), dto.getNewAttractionCondition());

        // Очистка старых связей
        employee.getEducationLinks().clear();
        employee.getProfessionalDevelopments().clear();
        employee.getProfessionalRetraining().clear();
        employee.getTaughtPrograms().clear();

        addEducationLevels(employee, dto.getNewEducationLevels());
        addProfessionalDevelopments(employee, dto.getProfessionalDevelopments());
        addProfessionalRetraining(employee, dto.getProfessionalRetraining());
        addTaughtPrograms(employee, dto.getTaughtProgramIds(), dto.getNewTaughtPrograms());


        return employeeRepository.save(employee);
    }

    public Optional<byte[]> findPhoto(Employee employee) {
        return Optional.ofNullable(employee.getPhoto())
                .filter(StringUtils::hasText)
                .flatMap(imageService::get);
    }

    // ====================== УДАЛЕНИЕ ======================
    @Transactional
    public void deleteEmployee(Integer employeeId) {
        Employee employee = findById(employeeId);

        employee.getEducationLinks().clear();
        employee.getProfessionalDevelopments().clear();
        employee.getProfessionalRetraining().clear();
        employee.getTaughtPrograms().clear();

        employeeRepository.delete(employee);
    }

    // ====================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ======================

    public Employee findById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Сотрудник не найден"));
    }

    public UpdateTeacherDTO convertToUpdateDTO(Employee emp) {
        UpdateTeacherDTO dto = new UpdateTeacherDTO();

        dto.setEId(emp.getEId());
        dto.setFirstName(emp.getFirstName());
        dto.setLastName(emp.getLastName());
        dto.setMiddleName(emp.getMiddleName());
        dto.setWorkExperience(emp.getWorkExperience());
        dto.setWorkExperienceInEducationalInstitution(emp.getWorkExperienceInEducationalInstitution());

        // ManyToOne
        if (emp.getAcademicDegree() != null) dto.setAcademicDegreeId(emp.getAcademicDegree().getAdId());
        if (emp.getPost() != null) dto.setPostId(emp.getPost().getPId());
        if (emp.getAttractionCondition() != null) dto.setAttractionConditionId(emp.getAttractionCondition().getAcId());

        if (emp.getEducationLinks() != null && !emp.getEducationLinks().isEmpty()) {
            dto.setNewEducationLevels(new ArrayList<>(emp.getEducationLinks().stream()
                    .map(l -> l.getEducationLevel().getEducation())
                    .toList()));
        }

        if (emp.getProfessionalDevelopments() != null && !emp.getProfessionalDevelopments().isEmpty()) {
            dto.setProfessionalDevelopments(new ArrayList<>(emp.getProfessionalDevelopments().stream()
                    .map(ProfessionalDevelopment::getQualifications)
                    .toList()));
        }

        if (emp.getProfessionalRetraining() != null && !emp.getProfessionalRetraining().isEmpty()) {
            dto.setProfessionalRetraining(new ArrayList<>(emp.getProfessionalRetraining().stream()
                    .map(ProfessionalRetraining::getQualifications)
                    .toList()));
        }

        if (emp.getTaughtPrograms() != null && !emp.getTaughtPrograms().isEmpty()) {
            dto.setTaughtProgramIds(new ArrayList<>(emp.getTaughtPrograms().stream()
                    .map(pe -> pe.getTaughtProgram().getTpId())
                    .toList()));
        }


        return dto;
    }

    // ====================== ManyToOne ======================
    private void handleAcademicDegree(Employee emp, Integer id, String newName) {
        if (id != null && id > 0) {
            academicDegreeRepository.findById(Long.valueOf(id)).ifPresent(emp::setAcademicDegree);
        } else if (StringUtils.hasText(newName)) {
            AcademicDegree degree = new AcademicDegree();
            degree.setAcademic(newName.trim());
            degree = academicDegreeRepository.save(degree);
            emp.setAcademicDegree(degree);
        }
    }

    private void handlePost(Employee emp, Integer id, String newName) {
        if (id != null && id > 0) {
            postRepository.findById(Long.valueOf(id)).ifPresent(emp::setPost);
        } else if (StringUtils.hasText(newName)) {
            Post post = new Post();
            post.setPost(newName.trim());
            post = postRepository.save(post);
            emp.setPost(post);
        }
    }

    private void handleAttractionCondition(Employee emp, Integer id, String newName) {
        if (id != null && id > 0) {
            attractionConditionRepository.findById(Long.valueOf(id)).ifPresent(emp::setAttractionCondition);
        } else if (StringUtils.hasText(newName)) {
            AttractionCondition condition = new AttractionCondition();
            condition.setCondition(newName.trim());
            condition = attractionConditionRepository.save(condition);
            emp.setAttractionCondition(condition);
        }
    }

    // ====================== Динамические связи ======================
    private void addEducationLevels(Employee emp, List<String> levels) {
        if (levels == null) return;
        for (String name : levels) {
            if (StringUtils.hasText(name)) {
                EducationLevel level = new EducationLevel();
                level.setEducation(name.trim());
                level = educationLevelRepository.save(level);

                EmployeesEducationLevel link = new EmployeesEducationLevel();
                link.setEmployee(emp);
                link.setEducationLevel(level);
                emp.addEducationLink(link);
            }
        }
    }

    private void addProfessionalDevelopments(Employee emp, List<String> quals) {
        if (quals == null) return;
        for (String q : quals) {
            if (StringUtils.hasText(q)) {
                ProfessionalDevelopment pd = new ProfessionalDevelopment();
                pd.setQualifications(q.trim());
                pd.setEmployee(emp);
                emp.addProfessionalDevelopment(pd);
            }
        }
    }

    private void addProfessionalRetraining(Employee emp, List<String> quals) {
        if (quals == null) return;
        for (String q : quals) {
            if (StringUtils.hasText(q)) {
                ProfessionalRetraining pr = new ProfessionalRetraining();
                pr.setQualifications(q.trim());
                pr.setEmployee(emp);
                emp.addProfessionalRetraining(pr);
            }
        }
    }

    private void addTaughtPrograms(Employee emp, List<Integer> existingIds, List<String> newNames) {
        if (existingIds != null) {
            for (Integer id : existingIds) {
                if (id != null && id > 0) {
                    taughtProgramRepository.findById(Long.valueOf(id)).ifPresent(tp -> {
                        ProgramEmployees pe = new ProgramEmployees();
                        pe.setEmployee(emp);
                        pe.setTaughtProgram(tp);
                        emp.addTaughtProgram(pe);
                    });
                }
            }
        }

        if (newNames != null) {
            for (String name : newNames) {
                if (StringUtils.hasText(name)) {
                    TaughtProgram tp = new TaughtProgram();
                    tp.setQualifications(name.trim());
                    tp = taughtProgramRepository.save(tp);

                    ProgramEmployees pe = new ProgramEmployees();
                    pe.setEmployee(emp);
                    pe.setTaughtProgram(tp);
                    emp.addTaughtProgram(pe);
                }
            }
        }
    }
}