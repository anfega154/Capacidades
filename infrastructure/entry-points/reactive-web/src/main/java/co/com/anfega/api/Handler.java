package co.com.anfega.api;

import co.com.anfega.api.dto.CreateAbilityDTO;
import co.com.anfega.api.helper.api.BaseHandler;
import co.com.anfega.api.mapper.AbilityDTOMapper;
import co.com.anfega.api.serice.AbilityService;
import co.com.anfega.model.ability.Ability;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler extends BaseHandler {

    private final AbilityService abilityService;
    private final AbilityDTOMapper abilityDTOMapper;

    public Mono<ServerResponse> listenSaveAbility(ServerRequest request) {
        return request.bodyToMono(CreateAbilityDTO.class)
                .map(abilityDTOMapper::toModel)
                .flatMap(abilityService::save)
                .map(abilityDTOMapper::toResponse)
                .flatMap(response -> created("Capacidad creada con exito", response));
    }

}
