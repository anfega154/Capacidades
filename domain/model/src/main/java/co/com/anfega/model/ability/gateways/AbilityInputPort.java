package co.com.anfega.model.ability.gateways;

import co.com.anfega.model.ability.Ability;
import reactor.core.publisher.Mono;
import co.com.anfega.model.common.PageResponse;

public interface AbilityInputPort {
    Mono<Ability> save(Ability ability);
    Mono<PageResponse<Ability>> listAbilities(
            int page,
            int size,
            String sortBy,
            String direction
    );
}
