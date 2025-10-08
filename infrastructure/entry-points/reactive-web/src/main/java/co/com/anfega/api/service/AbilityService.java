package co.com.anfega.api.service;

import co.com.anfega.consumer.client.WebClientHelper;
import co.com.anfega.consumer.ApiResponse;
import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.ability.gateways.AbilityInputPort;
import co.com.anfega.model.technology.Technology;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

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
                            .filter(Objects::nonNull)
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

    public Mono<List<Ability>> findByIds(List<Long> ids) {
        return abilityInputPort.findByIds(ids)
                .collectList()
                .zipWith(getTechnologies())
                .map(tuple -> enrichAbilities(tuple.getT1(), tuple.getT2()));
    }

    private List<Ability> enrichAbilities(List<Ability> abilities, List<Technology> technologies) {
        abilities.forEach(ability -> {
            List<Technology> enriched = ability.getTechnologies().stream()
                    .map(tech -> technologies.stream()
                            .filter(t -> t.getId().equals(tech.getId()))
                            .findFirst()
                            .map(match -> {
                                tech.setId(match.getId());
                                tech.setName(match.getName());
                                tech.setDescription(match.getDescription());
                                return tech;
                            })
                            .orElse(tech))
                    .toList();
            ability.setTechnologies(enriched);
        });
        return abilities;
    }

    public Mono<Void> deleteByIds(List<Long> ids) {
        return abilityInputPort.deleteByIds(ids);
    }

    private Mono<List<Technology>> getTechnologies() {
        return webClientHelper.get(
                        "http://localhost:8088/api/v1/tecnologias",
                        null,
                        new ParameterizedTypeReference<ApiResponse<Technology>>() {
                        })
                .map(ApiResponse::getContent);
    }

}
