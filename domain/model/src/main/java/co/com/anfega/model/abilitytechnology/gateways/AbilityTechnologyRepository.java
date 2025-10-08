package co.com.anfega.model.abilitytechnology.gateways;

import co.com.anfega.model.abilitytechnology.AbilityTechnology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AbilityTechnologyRepository {
    Flux<AbilityTechnology> save(Long idAbility, List<Long> idTechnologies);
    Flux<AbilityTechnology> findByIdAbility(Long idAbility);
}
