package co.com.anfega.r2dbc;

import co.com.anfega.r2dbc.entity.AbilityEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MyReactiveRepository extends ReactiveCrudRepository<AbilityEntity, Long>, ReactiveQueryByExampleExecutor<AbilityEntity> {

}
