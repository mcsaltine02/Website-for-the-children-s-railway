package by.mcsaltine.vkpost.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "train_information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TainInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ti_id")
    private Integer ti_id;

    @Column(name = "price")
    private Integer price;
}
