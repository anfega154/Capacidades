package co.com.anfega.api.dto;

import co.com.anfega.model.technology.Technology;

import java.util.List;

public record AbilityDTO(Long id, String description, List<Technology> technologies) {
}
