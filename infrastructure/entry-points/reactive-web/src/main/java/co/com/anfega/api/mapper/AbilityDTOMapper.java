package co.com.anfega.api.mapper;

import co.com.anfega.api.dto.AbilityDTO;
import co.com.anfega.api.dto.CreateAbilityDTO;
import co.com.anfega.model.ability.Ability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AbilityDTOMapper {
    AbilityDTO toResponse(Ability ability);
    Ability toModel(CreateAbilityDTO createAbilityDTO);
}
