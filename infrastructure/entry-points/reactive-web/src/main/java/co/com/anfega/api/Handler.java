package co.com.anfega.api;

import co.com.anfega.api.dto.CreateAbilityDTO;
import co.com.anfega.api.helper.api.BaseHandler;
import co.com.anfega.api.mapper.AbilityDTOMapper;
import co.com.anfega.api.service.AbilityService;
import co.com.anfega.model.technology.Technology;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler extends BaseHandler {

    private final AbilityService abilityService;
    private final AbilityDTOMapper abilityDTOMapper;
    private final Validator validator;

    public Mono<ServerResponse> listenSaveAbility(ServerRequest request) {
        return bodyToMonoValidated(validator, request, CreateAbilityDTO.class)
                .map(abilityDTOMapper::toModel)
                .flatMap(abilityService::save)
                .map(abilityDTOMapper::toResponse)
                .flatMap(response -> created("Capacidad creada con exito", response));
    }

    @CircuitBreaker(name = "externalServiceCB", fallbackMethod = "fallbackGetData")
    public Mono<ServerResponse> listenListAbilities(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        String direction = request.queryParam("direction").orElse("asc");

        return abilityService.listAbilities(page, size, sortBy, direction)
                .flatMap(abilities -> ok(abilities.isEmpty() ? "No se encontraron capacidades" : "Capacidades encontradas", abilities));
    }

    public Mono<List<Technology>> fallbackGetData(Throwable ex) {
        log.warn("Fallback ejecutado por error en getTechnologies: {}", ex.getMessage());
        return Mono.error(new RuntimeException("⚠ Servicio de tecnologías no disponible, intente más tarde ⚠", ex));
    }

}
