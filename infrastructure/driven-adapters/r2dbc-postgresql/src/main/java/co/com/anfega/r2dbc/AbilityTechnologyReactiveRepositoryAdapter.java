package co.com.anfega.r2dbc;

import co.com.anfega.model.abilitytechnology.AbilityTechnology;
import co.com.anfega.model.abilitytechnology.gateways.AbilityTechnologyRepository;
import co.com.anfega.r2dbc.entity.AbilityTechnologyEntity;
import co.com.anfega.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class AbilityTechnologyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        AbilityTechnology,
        AbilityTechnologyEntity,
        Void,
        AbilityTechnologyReactiveRepository
        > implements AbilityTechnologyRepository {

    public AbilityTechnologyReactiveRepositoryAdapter(AbilityTechnologyReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, AbilityTechnology.class));
    }

    @Override
    public Flux<AbilityTechnology> save(Long idAbility, List<Long> idTechnologies) {
        return Flux.fromIterable(idTechnologies)
                .flatMap(idTechnology -> {
                    AbilityTechnologyEntity entity = new AbilityTechnologyEntity();
                    entity.setAbilityId(idAbility);
                    entity.setTechnologyId(idTechnology);
                    return repository.save(entity);
                })
                .map(savedEntity -> new AbilityTechnology(
                        savedEntity.getAbilityId(),
                        savedEntity.getTechnologyId()
                ))
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error creando el registro: " + e.getMessage())));
    }

    @Override
    public Flux<AbilityTechnology> findByIdAbility(Long idAbility) {
        return repository.findByAbilityId(idAbility)
                .map(entity -> new AbilityTechnology(
                        entity.getAbilityId(),
                        entity.getTechnologyId()
                ))
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error buscando el registro: " + e.getMessage())));
    }
}
