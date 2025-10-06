package co.com.anfega.api.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class DeleteAbilitiesDTO {
    @Size(min = 1, message = "La lista no puede estar vacía")
    private List<Long> ids;
}
