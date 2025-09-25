package co.com.anfega.model.ability.gateways;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.common.PageResponse;
import reactor.core.publisher.Mono;

public interface AbilityRepository {
    Mono<Ability> save(Ability ability);
    Mono<PageResponse<Ability>> findAllPaginated(int page, int size, String sortBy, String direction);
}
