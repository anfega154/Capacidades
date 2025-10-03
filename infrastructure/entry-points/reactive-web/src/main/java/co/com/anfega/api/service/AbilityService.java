package co.com.anfega.api.service;

import co.com.anfega.api.helper.client.ApiResponse;
import co.com.anfega.api.helper.client.WebClientHelper;
import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.ability.gateways.AbilityInputPort;
import co.com.anfega.model.technology.Technology;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AbilityService {

    private final AbilityInputPort abilityInputPort;
    private final WebClientHelper webClientHelper;

    public Mono<Ability> save(Ability ability) {
        return getTechnologies()
                .flatMap(existingTechnologies -> {
                    List<Technology> inputTechnologies = ability.getTechnologies();

                    List<Technology> filteredTechnologies = inputTechnologies.stream()
                            .map(inputTech -> existingTechnologies.stream()
                                    .filter(existingTech -> existingTech.getName()
                                            .equalsIgnoreCase(inputTech.getName()))
                                    .findFirst()
                                    .map(match -> {
                                        inputTech.setId(match.getId());
                                        inputTech.setDescription(match.getDescription());
                                        return inputTech;
                                    })
                                    .orElse(null))
                            .filter(t -> t != null)
                            .toList();

                    ability.setTechnologies(filteredTechnologies);
                    return abilityInputPort.save(ability);
                });
    }

    public Mono<List<Ability>> listAbilities(int page, int size, String sortBy, String direction) {
        return Mono.zip(
                abilityInputPort.listAbilities(page, size, sortBy, direction),
                getTechnologies()
        ).map(tuple -> enrichAbilities(tuple.getT1().getContent(), tuple.getT2()));
    }

    public Mono<List<Ability>> findByNames(List<String> names) {
        return abilityInputPort.findByNames(names)
                .collectList()
                .zipWith(getTechnologies())
                .map(tuple -> enrichAbilities(tuple.getT1(), tuple.getT2()));
    }

    private List<Ability> enrichAbilities(List<Ability> abilities, List<Technology> technologies) {
        abilities.forEach(ability -> {
            List<Technology> enriched = ability.getTechnologies().stream()
                    .map(tech -> technologies.stream()
                            .filter(t -> t.getName().equalsIgnoreCase(tech.getName()))
                            .findFirst()
                            .map(match -> {
                                tech.setId(match.getId());
                                tech.setDescription(match.getDescription());
                                return tech;
                            })
                            .orElse(tech))
                    .toList();
            ability.setTechnologies(enriched);
        });
        return abilities;
    }

    @Cacheable(value = "technologiesCache", unless = "#result == null")
    public Mono<List<Technology>> getTechnologies() {
        return webClientHelper.get(
                        "http://localhost:8088/api/v1/tecnologias",
                        null,
                        new ParameterizedTypeReference<ApiResponse<Technology>>() {
                        })
                .map(ApiResponse::getContent);
    }

}
