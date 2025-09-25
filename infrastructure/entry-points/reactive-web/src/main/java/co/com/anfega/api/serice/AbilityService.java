package co.com.anfega.api.serice;

import co.com.anfega.api.helper.client.ApiResponse;
import co.com.anfega.api.helper.client.WebClientHelper;
import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.ability.gateways.AbilityInputPort;
import co.com.anfega.model.technology.Technology;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AbilityService {

    private final AbilityInputPort abilityInputPort;
    private final WebClientHelper webClientHelper;

    public Mono<Ability> save(Ability ability) {
        return getTechnologies()
                .collectList()
                .flatMap(existingTechnologies -> {
                    List<Technology> inputTechnologies = ability.getTechnologies();

                    List<Technology> filteredTechnologies = inputTechnologies.stream()
                            .filter(inputTech -> existingTechnologies.stream()
                                    .anyMatch(existingTech ->
                                            existingTech.getName().equalsIgnoreCase(inputTech.getName())))
                            .toList();

                    ability.setTechnologies(filteredTechnologies);
                    return abilityInputPort.save(ability);
                });
    }

    public Mono<List<Ability>> listAbilities(int page, int size, String sortBy, String direction) {
        return abilityInputPort.listAbilities(page, size, sortBy, direction)
                .map(pageResponse -> pageResponse.getContent().isEmpty() ? List.of() : pageResponse.getContent());
    }

    private Flux<Technology> getTechnologies() {
        return webClientHelper.get(
                        "http://tecnologia-app:8081/api/v1/tecnologias",
                        null,
                        new ParameterizedTypeReference<ApiResponse<Technology>>() {
                        })
                .map(ApiResponse::getContent)
                .flatMapMany(Flux::fromIterable);
    }


}
