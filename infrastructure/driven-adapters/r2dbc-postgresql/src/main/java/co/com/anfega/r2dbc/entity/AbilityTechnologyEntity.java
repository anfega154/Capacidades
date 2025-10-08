package co.com.anfega.r2dbc.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Table(name = "tecnologia_capacidad")
public class AbilityTechnologyEntity {

    @Column("capacidad_id")
    private Long abilityId;

    @Column("tecnologia_id")
    private Long technologyId;
}