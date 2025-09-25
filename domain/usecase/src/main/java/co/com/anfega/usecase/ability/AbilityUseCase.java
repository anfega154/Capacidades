package co.com.anfega.usecase.ability;


import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.ability.gateways.AbilityInputPort;
import co.com.anfega.model.ability.gateways.AbilityRepository;
import co.com.anfega.model.technology.Technology;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbilityUseCase implements AbilityInputPort {

    private final AbilityRepository abilityRepository;

    public AbilityUseCase(AbilityRepository abilityRepository) {
        this.abilityRepository = abilityRepository;
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
        for (Technology t : technologies) {
            if (!names.add(t.getName().toLowerCase())) {
                return Mono.error(new IllegalArgumentException("No se permiten tecnologías repetidas."));
            }
        }
        return abilityRepository.save(ability);
    }

}

