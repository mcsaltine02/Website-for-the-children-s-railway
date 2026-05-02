package by.mcsaltine.vkpost.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"educationLinks", "professionalDevelopments",
        "professionalRetraining"})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "e_id")
    private Integer eId;

    @Column(name = "photo_path")
    private String photo;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "work_experience")
    private Integer workExperience;

    @Column(name = "work_experience_in_educational_institution")
    private Integer workExperienceInEducationalInstitution;

    @OneToOne
    @JoinColumn(name = "ad_id")
    private AcademicDegree academicDegree;

    @ManyToOne
    @JoinColumn(name = "p_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "ac_id")
    private AttractionCondition attractionCondition;

    // Связи
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<EmployeesEducationLevel> educationLinks = new HashSet<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ProfessionalDevelopment> professionalDevelopments = new HashSet<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ProfessionalRetraining> professionalRetraining = new HashSet<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, orphanRemoval = false)
    private Set<ProgramEmployees> taughtPrograms = new HashSet<>();

    public void addProfessionalDevelopment(ProfessionalDevelopment pd) {
        this.professionalDevelopments.add(pd);
        pd.setEmployee(this);
    }

    public void addProfessionalRetraining(ProfessionalRetraining pr) {
        this.professionalRetraining.add(pr);
        pr.setEmployee(this);
    }

    public void addEducationLink(EmployeesEducationLevel link) {
        this.educationLinks.add(link);
        link.setEmployee(this);
    }

    public void addTaughtProgram(ProgramEmployees pe) {
        if (this.taughtPrograms == null) {
            this.taughtPrograms = new HashSet<>();
        }
        this.taughtPrograms.add(pe);
        if (pe.getEmployee() == null) {
            pe.setEmployee(this);
        }
    }

    public void setTaughtPrograms(Set<ProgramEmployees> taughtPrograms) {
        if (taughtPrograms != null) {
            this.taughtPrograms = new HashSet<>(taughtPrograms);
        } else {
            this.taughtPrograms = new HashSet<>();
        }
    }

}

