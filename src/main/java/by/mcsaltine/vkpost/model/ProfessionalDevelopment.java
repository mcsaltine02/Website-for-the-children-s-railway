package by.mcsaltine.vkpost.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "professional_development")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProfessionalDevelopment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pd_id")
    private Integer pdId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "e_id")
    private Employee employee;

    @Column(name = "qualifications")
    private String qualifications;
}
