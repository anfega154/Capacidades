package co.com.anfega.usecase.ability;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.ability.gateways.AbilityRepository;
import co.com.anfega.model.common.PageResponse;
import co.com.anfega.model.technology.Technology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AbilityUseCaseTest {

    private AbilityRepository abilityRepository;
    private AbilityUseCase abilityUseCase;

    @BeforeEach
    void setUp() {
        abilityRepository = Mockito.mock(AbilityRepository.class);
        abilityUseCase = new AbilityUseCase(abilityRepository);
    }

    private Ability buildAbilityWithTechnologies(List<Technology> technologies) {
        Ability ability = new Ability();
        ability.setTechnologies(technologies);
        return ability;
    }

    @Test
    void save_shouldSucceed_whenValidTechnologies() {
        List<Technology> technologies = List.of(
                new Technology("Java", "1.8"),
                new Technology("Spring", "5.0"),
                new Technology("Docker", "19.03")
        );
        Ability ability = buildAbilityWithTechnologies(technologies);

        when(abilityRepository.save(any(Ability.class))).thenReturn(Mono.just(ability));

        Mono<Ability> result = abilityUseCase.save(ability);

        StepVerifier.create(result)
                .expectNext(ability)
                .verifyComplete();

        verify(abilityRepository, times(1)).save(ability);
    }

    @Test
    void save_shouldFail_whenLessThanThreeTechnologies() {
        Ability ability = buildAbilityWithTechnologies(List.of(
                new Technology("Java", "1.8"),
                new Technology("Spring", "5.0")
        ));

        Mono<Ability> result = abilityUseCase.save(ability);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("al menos 3"))
                .verify();

        verify(abilityRepository, never()).save(any());
    }

    @Test
    void save_shouldFail_whenTooManyTechnologies() {
        List<Technology> technologies = new ArrayList<>();
        IntStream.range(0, 21).forEach(i -> technologies.add(new Technology("Tech" + i, "1.0")));
        Ability ability = buildAbilityWithTechnologies(technologies);

        Mono<Ability> result = abilityUseCase.save(ability);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("más de 20"))
                .verify();

        verify(abilityRepository, never()).save(any());
    }

    @Test
    void save_shouldFail_whenDuplicateTechnologies() {
        Ability ability = buildAbilityWithTechnologies(List.of(
                new Technology("Java", "1.8"),
                new Technology("Spring", "5.0"),
                new Technology("java", "")
        ));

        Mono<Ability> result = abilityUseCase.save(ability);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("repetidas"))
                .verify();

        verify(abilityRepository, never()).save(any());
    }

    @Test
    void listAbilities_shouldReturnPage_whenRepositoryReturnsData() {
        Ability ability = buildAbilityWithTechnologies(List.of(
                new Technology("Java", "1.8"),
                new Technology("Spring", "5.0"),
                new Technology("Docker", "19.03")
        ));
        PageResponse<Ability> response = new PageResponse<>(List.of(ability), 0, 10, 1);

        when(abilityRepository.findAllPaginated(0, 10, "name", "asc"))
                .thenReturn(Mono.just(response));

        Mono<PageResponse<Ability>> result = abilityUseCase.listAbilities(0, 10, "name", "asc");

        StepVerifier.create(result)
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void listAbilities_shouldReturnEmptyPage_whenRepositoryReturnsEmpty() {
        when(abilityRepository.findAllPaginated(0, 10, "name", "asc"))
                .thenReturn(Mono.empty());

        Mono<PageResponse<Ability>> result = abilityUseCase.listAbilities(0, 10, "name", "asc");

        StepVerifier.create(result)
                .assertNext(page -> {
                    assert page.getContent().isEmpty();
                    assert page.getTotalElements() == 0;
                    assert page.getPage() == 0;
                    assert page.getSize() == 10;
                })
                .verifyComplete();
    }
}

