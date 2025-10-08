package co.com.anfega.r2dbc;

import co.com.anfega.r2dbc.entity.AbilityTechnologyEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AbilityTechnologyReactiveRepository extends ReactiveCrudRepository<AbilityTechnologyEntity, Void>, ReactiveQueryByExampleExecutor<AbilityTechnologyEntity> {
    Flux<AbilityTechnologyEntity> findByAbilityId(Long abilityId);
}
