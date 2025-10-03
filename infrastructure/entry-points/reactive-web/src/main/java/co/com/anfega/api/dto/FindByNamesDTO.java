package co.com.anfega.api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class FindByNamesDTO {
    @NotEmpty(message = "La lista de nombres no puede estar vacía")
    List<String> names;
}
