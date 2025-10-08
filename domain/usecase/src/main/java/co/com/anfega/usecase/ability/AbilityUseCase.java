package co.com.anfega.usecase.ability;


import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.ability.gateways.AbilityInputPort;
import co.com.anfega.model.ability.gateways.AbilityRepository;
import co.com.anfega.model.abilitytechnology.gateways.AbilityTechnologyRepository;
import co.com.anfega.model.common.PageResponse;
import co.com.anfega.model.technology.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbilityUseCase implements AbilityInputPort {

    private final AbilityRepository abilityRepository;
    private final AbilityTechnologyRepository abilityTechnologyRepository;

    public AbilityUseCase(AbilityRepository abilityRepository, AbilityTechnologyRepository abilityTechnologyRepository) {
        this.abilityRepository = abilityRepository;
        this.abilityTechnologyRepository = abilityTechnologyRepository;
    }

    @Override
    public Mono<Ability> save(Ability ability) {

        List<Technology> technologies = ability.getTechnologies();

        if (technologies == null || technologies.size() < 3) {
            return Mono.error(new IllegalArgumentException("La capacidad debe tener al menos 3 tecnologías."));
        }

        if (technologies.size() > 20) {
            return Mono.error(new IllegalArgumentException("La capacidad no puede tener más de 20 tecnologías."));
        }

        Set<String> names = new HashSet<>();
        List<Technology> uniqueTechnologies = technologies.stream()
                .filter(t -> names.add(t.getName().toLowerCase()))
                .toList();
        ability.setTechnologies(uniqueTechnologies);

        return abilityRepository.save(ability)
                .flatMap(savedAbility ->
                    abilityTechnologyRepository.save(
                            savedAbility.getId(),
                            uniqueTechnologies.stream().map(Technology::getId).toList()
                    )
                    .collectList()
                    .thenReturn(savedAbility)
                );
    }

    @Override
    public Mono<PageResponse<Ability>> listAbilities(int page, int size, String sortBy, String direction) {
        return abilityRepository.findAllPaginated(page, size, sortBy, direction)
                .flatMap(pageResponse -> {
                    List<Ability> abilities = pageResponse.getContent();
                    return Flux.fromIterable(abilities)
                            .flatMap(ability -> abilityTechnologyRepository.findByIdAbility(ability.getId())
                                    .collectList()
                                    .map(abilityTechnologies -> {
                                        List<Technology> technologies = abilityTechnologies.stream()
                                                .map(at -> {
                                                    Technology tech = new Technology();
                                                    tech.setId(at.getTechnologyId());
                                                    return tech;
                                                })
                                                .toList();
                                        ability.setTechnologies(technologies);
                                        return ability;
                                    })
                            )
                            .collectList()
                            .map(updatedAbilities ->
                                    new PageResponse<>(
                                            updatedAbilities,
                                            pageResponse.getPage(),
                                            pageResponse.getSize(),
                                            pageResponse.getTotalElements()
                                    )
                            );
                })
                .switchIfEmpty(Mono.just(new PageResponse<>(List.of(), page, size, 0)));
    }

    @Override
    public Flux<Ability> findByIds(List<Long> ids) {
        return abilityRepository.findByIds(ids)
                .switchIfEmpty(Flux.error(new IllegalStateException("No hay capacidades registradas")))
                .flatMap(ability ->
                        abilityTechnologyRepository.findByIdAbility(ability.getId())
                                .collectList()
                                .map(abilityTechnologies -> {
                                    List<Technology> technologies = abilityTechnologies.stream()
                                            .map(at -> {
                                                Technology tech = new Technology();
                                                tech.setId(at.getTechnologyId());
                                                return tech;
                                            })
                                            .toList();
                                    ability.setTechnologies(technologies);
                                    return ability;
                                })
                );
    }


    @Override
    public Mono<Void> deleteByIds(List<Long> ids) {
        return abilityRepository.deleteByIds(ids);
    }

}

