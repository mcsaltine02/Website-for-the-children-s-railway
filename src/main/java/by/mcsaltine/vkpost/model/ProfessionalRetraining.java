package by.mcsaltine.vkpost.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "professional_retraining")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProfessionalRetraining {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pr_id")
    private Integer prId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "e_id")
    private Employee employee;

    @Column(name = "qualifications")
    private String qualifications;
}
