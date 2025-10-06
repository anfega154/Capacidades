package co.com.anfega.model.ability.gateways;

import co.com.anfega.model.ability.Ability;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import co.com.anfega.model.common.PageResponse;

import java.util.List;

public interface AbilityInputPort {
    Mono<Ability> save(Ability ability);
    Mono<PageResponse<Ability>> listAbilities(
            int page,
            int size,
            String sortBy,
            String direction,
            int totalElements
    );
    Flux<Ability> findByNames(List<String> names);
    Mono<Void> deleteByIds(List<Long> ids);
}
