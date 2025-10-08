package co.com.anfega.r2dbc;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.ability.gateways.AbilityRepository;
import co.com.anfega.model.common.PageResponse;
import co.com.anfega.model.common.PaginationHelper;
import co.com.anfega.r2dbc.entity.AbilityEntity;
import co.com.anfega.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

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

        return repository.save(data)
                .map(savedData -> new Ability(
                        savedData.getId(),
                        savedData.getName(),
                        savedData.getDescription(),
                        Collections.emptyList()
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
    public Flux<Ability> findByIds(List<Long> ids) {
        return repository.findByIdIn(ids)
                .map(this::toAbility)
                .switchIfEmpty(Flux.empty())
                .onErrorResume(e -> Flux.empty());
    }

    private Ability toAbility(AbilityEntity entity) {
        return new Ability(entity.getId(), entity.getName(), entity.getDescription(), new ArrayList<>());
    }

    private PageResponse<Ability> paginateAndSortAbilities(
            List<Ability> list, int page, int size, String sortBy, String direction) {

        if (sortBy == null) sortBy = "";
        sortBy = sortBy.trim().toLowerCase();

        if ("technologiescount".equals(sortBy)) {
            List<Ability> copy = new ArrayList<>(list == null ? Collections.emptyList() : list);

            Comparator<Ability> cmp = Comparator.comparingInt(
                    a -> a.getTechnologies() == null ? 0 : a.getTechnologies().size()
            );

            if ("desc".equalsIgnoreCase(direction)) {
                cmp = cmp.reversed();
            }

            copy.sort(cmp);
            return PageResponse.of(copy, page, size);
        }

        return switch (sortBy) {
            case "name" -> PaginationHelper.paginateAndSort(list, page, size, direction, Ability::getName);
            case "description" ->
                    PaginationHelper.paginateAndSort(list, page, size, direction, Ability::getDescription);
            default -> PaginationHelper.paginateAndSort(list, page, size, direction,
                    a -> a.getId() == null ? "" : String.valueOf(a.getId()));
        };
    }


    @Override
    public Mono<Void> deleteByIds(List<Long> ids) {
        return repository.deleteAllById(ids)
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error eliminando capacidades: " + e.getMessage())));

    }
}
