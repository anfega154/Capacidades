package co.com.anfega.api;

import co.com.anfega.api.dto.CreateAbilityDTO;
import co.com.anfega.api.helper.api.BaseHandler;
import co.com.anfega.api.mapper.AbilityDTOMapper;
import co.com.anfega.api.serice.AbilityService;
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

    public Mono<ServerResponse> listenListAbilities(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        String direction = request.queryParam("direction").orElse("asc");

        return abilityService.listAbilities(page, size, sortBy, direction)
                .flatMap(abilities -> ok(abilities.isEmpty() ? "No se encontraron capacidades" : "Capacidades encontradas", abilities));
    }

}
