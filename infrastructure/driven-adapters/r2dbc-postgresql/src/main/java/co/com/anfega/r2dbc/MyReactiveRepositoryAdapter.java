package co.com.anfega.r2dbc;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.ability.gateways.AbilityRepository;
import co.com.anfega.model.common.PageResponse;
import co.com.anfega.model.common.PaginationHelper;
import co.com.anfega.model.technology.Technology;
import co.com.anfega.r2dbc.entity.AbilityEntity;
import co.com.anfega.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
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
                                .map(name -> new Technology(name, null))
                                .toList()
                                : new ArrayList<>()
                ));
    }

    @Override
    public Mono<PageResponse<Ability>> findAllPaginated(int page, int size, String sortBy, String direction) {
        return repository.findAll()
                .map(this::toAbility)
                .collectList()
                .map(list -> paginateAndSortAbilities(list, page, size, sortBy, direction));
    }

    @Override
    public Flux<Ability> findByNames(List<String> names) {
        return repository.findByNameIn(names)
                .map(this::toAbility)
                .switchIfEmpty(Flux.empty())
                .onErrorResume(e -> Flux.empty());
    }

    private Ability toAbility(AbilityEntity entity) {
        List<Technology> technologies = (entity.getTechnologies() != null && !entity.getTechnologies().isEmpty())
                ? Arrays.stream(entity.getTechnologies().split(","))
                .map(name -> new Technology(name, name))
                .toList()
                : java.util.Collections.emptyList();
        return new Ability(entity.getId(), entity.getName(), entity.getDescription(), technologies);
    }

    private PageResponse<Ability> paginateAndSortAbilities(
            java.util.List<Ability> list, int page, int size, String sortBy, String direction) {
        final String TECHNOLOGIES_COUNT = "technologiesCount";
        switch (sortBy == null ? "" : sortBy.toLowerCase()) {
            case TECHNOLOGIES_COUNT:
                return PaginationHelper.paginateAndSort(list, page, size, direction, a -> a.getTechnologies().size());
            case "name":
                return PaginationHelper.paginateAndSort(list, page, size, direction, Ability::getName);
            case "description":
                return PaginationHelper.paginateAndSort(list, page, size, direction, Ability::getDescription);
            default:
                return PaginationHelper.paginateAndSort(list, page, size, direction, a -> String.valueOf(a.getId()));
        }
    }


}
