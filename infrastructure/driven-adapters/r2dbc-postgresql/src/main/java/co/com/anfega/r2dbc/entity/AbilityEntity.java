package co.com.anfega.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Table(name = "capacidad")
public class AbilityEntity {
    @Id
    private Long id;
    @Column("nombre")
    private String name;
    @Column("descripcion")
    private String description;
    @Column("tecnologias")
    private String technologies;
}
