package by.mcsaltine.vkpost.controller.employees.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeacherDTO {

    private Integer eId;   // обязательно!

    private String firstName;
    private String lastName;
    private String middleName;

    private Integer workExperience;
    private Integer workExperienceInEducationalInstitution;

    private Integer academicDegreeId;
    private String newAcademicDegree;

    private Integer postId;
    private String newPost;

    private Integer attractionConditionId;
    private String newAttractionCondition;

    private List<String> newEducationLevels = new ArrayList<>();
    private List<String> professionalDevelopments = new ArrayList<>();
    private List<String> professionalRetraining = new ArrayList<>();

    private List<Integer> taughtProgramIds = new ArrayList<>();
    private List<String> newTaughtPrograms = new ArrayList<>();


    private String oldPhoto;
    private MultipartFile newPhoto;

}