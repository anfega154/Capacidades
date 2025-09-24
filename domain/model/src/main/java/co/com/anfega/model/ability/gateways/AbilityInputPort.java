package co.com.anfega.model.ability.gateways;

import co.com.anfega.model.ability.Ability;
import reactor.core.publisher.Mono;

public interface AbilityInputPort {
    Mono<Ability> save(Ability ability);
}
