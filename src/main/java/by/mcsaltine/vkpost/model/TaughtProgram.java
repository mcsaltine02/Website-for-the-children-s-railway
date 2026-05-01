package by.mcsaltine.vkpost.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "taught_program")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaughtProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tp_id")
    private Integer tpId;

    @Column(name = "qualifications")
    private String qualifications;

    @Column(name = "vacant_places")
    private Integer vacantPlaces;

    @OneToMany(mappedBy = "taughtProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProgramEmployees> employees = new HashSet<>();
}
