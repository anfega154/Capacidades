package co.com.anfega.r2dbc;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.r2dbc.entity.AbilityEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;


public interface MyReactiveRepository extends ReactiveCrudRepository<AbilityEntity, Long>, ReactiveQueryByExampleExecutor<AbilityEntity> {
    Flux<AbilityEntity> findByNameIn(List<String> names);
}
