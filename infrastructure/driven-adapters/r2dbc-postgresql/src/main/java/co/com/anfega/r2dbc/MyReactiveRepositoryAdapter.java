package co.com.anfega.r2dbc;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.ability.gateways.AbilityRepository;
import co.com.anfega.model.technology.Technology;
import co.com.anfega.r2dbc.entity.AbilityEntity;
import co.com.anfega.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Ability,
        AbilityEntity,
        Long,
        MyReactiveRepository
        > implements AbilityRepository {
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Ability.class));
    }

    @Override
    public Mono<Ability> save(Ability ability) {
        AbilityEntity data = new AbilityEntity();
        data.setId(ability.getId());
        data.setName(ability.getName());
        data.setDescription(ability.getDescription());

        if (ability.getTechnologies() != null && !ability.getTechnologies().isEmpty()) {
            String technologiesCsv = ability.getTechnologies()
                    .stream()
                    .map(Technology::getName)
                    .collect(Collectors.joining(","));
            data.setTechnologies(technologiesCsv);
        } else {
            data.setTechnologies(null);
        }

        return repository.save(data)
                .map(savedData -> new Ability(
                        savedData.getId(),
                        savedData.getName(),
                        savedData.getDescription(),
                        savedData.getTechnologies() != null && !savedData.getTechnologies().isEmpty()
                                ? Arrays.stream(savedData.getTechnologies().split(","))
                                .map(name -> new Technology(null, name, null))
                                .collect(Collectors.toList())
                                : new ArrayList<>()
                ));
    }

}
