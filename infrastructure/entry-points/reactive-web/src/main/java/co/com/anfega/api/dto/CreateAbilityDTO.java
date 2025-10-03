package co.com.anfega.api.dto;

import co.com.anfega.model.technology.Technology;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateAbilityDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String description;
    private List<Technology> technologies;
}
