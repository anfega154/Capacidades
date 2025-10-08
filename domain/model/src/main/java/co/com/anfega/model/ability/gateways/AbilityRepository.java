package co.com.anfega.model.ability.gateways;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.common.PageResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AbilityRepository {
    Mono<Ability> save(Ability ability);
    Mono<PageResponse<Ability>> findAllPaginated(int page, int size, String sortBy, String direction);
    Flux<Ability> findByIds(List<Long> ids);
    Mono<Void> deleteByIds(List<Long> ids);
}
