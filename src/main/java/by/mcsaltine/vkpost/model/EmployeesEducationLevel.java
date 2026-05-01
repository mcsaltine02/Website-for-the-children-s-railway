package by.mcsaltine.vkpost.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees_education_level",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_employee_education",
                columnNames = {"e_id", "el_id"}
        ))
public class EmployeesEducationLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eel_id")
    private Integer eelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "e_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "el_id")
    private EducationLevel  educationLevel;
}
