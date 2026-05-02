package by.mcsaltine.vkpost.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "program_employees")
@NoArgsConstructor
@AllArgsConstructor
@Data

public class ProgramEmployees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pe_id")
    private Integer peId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "e_id")
    private  Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tp_id")
    private TaughtProgram taughtProgram;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgramEmployees)) return false;
        ProgramEmployees that = (ProgramEmployees) o;
        return Objects.equals(peId, that.peId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(peId);
    }

    @Override
    public String toString() {
        return "ProgramEmployees{" +
                "peId=" + peId +
                ", taughtProgramId=" + (taughtProgram != null ? taughtProgram.getTpId() : null) +
                '}';
    }

}
