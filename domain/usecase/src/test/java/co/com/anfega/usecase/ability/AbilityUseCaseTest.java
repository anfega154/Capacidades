package co.com.anfega.usecase.ability;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.ability.gateways.AbilityRepository;
import co.com.anfega.model.abilitytechnology.gateways.AbilityTechnologyRepository;
import co.com.anfega.model.common.PageResponse;
import co.com.anfega.model.technology.Technology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AbilityUseCaseTest {

    private AbilityRepository abilityRepository;
    private AbilityUseCase abilityUseCase;
    private AbilityTechnologyRepository abilityTechnologyRepository;

    @BeforeEach
    void setUp() {
        abilityRepository = mock(AbilityRepository.class);
        abilityTechnologyRepository = mock(AbilityTechnologyRepository.class);
        abilityUseCase = new AbilityUseCase(abilityRepository, abilityTechnologyRepository);
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

        StepVerifier.create(abilityUseCase.save(ability))
                .expectNext(ability)
                .verifyComplete();

        verify(abilityRepository).save(ability);
    }

    @Test
    void save_shouldFail_whenLessThanThreeTechnologies() {
        Ability ability = buildAbilityWithTechnologies(List.of(
                new Technology("Java", "1.8"),
                new Technology("Spring", "5.0")
        ));

        StepVerifier.create(abilityUseCase.save(ability))
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

        StepVerifier.create(abilityUseCase.save(ability))
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
                new Technology("java", "otra")
        ));

        StepVerifier.create(abilityUseCase.save(ability))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("repetidas"))
                .verify();

        verify(abilityRepository, never()).save(any());
    }

    @Test
    void save_shouldFail_whenTechnologiesIsNull() {
        Ability ability = buildAbilityWithTechnologies(null);

        StepVerifier.create(abilityUseCase.save(ability))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("al menos 3"))
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

        StepVerifier.create(abilityUseCase.listAbilities(0, 10, "name", "asc"))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void listAbilities_shouldReturnEmptyPage_whenRepositoryReturnsEmpty() {
        when(abilityRepository.findAllPaginated(0, 10, "name", "asc"))
                .thenReturn(Mono.empty());

        StepVerifier.create(abilityUseCase.listAbilities(0, 10, "name", "asc"))
                .assertNext(page -> {
                    assert page.getContent().isEmpty();
                    assert page.getTotalElements() == 0;
                    assert page.getPage() == 0;
                    assert page.getSize() == 10;
                })
                .verifyComplete();
    }

    @Test
    void listAbilities_shouldFail_whenRepositoryErrors() {
        when(abilityRepository.findAllPaginated(0, 10, "name", "asc"))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(abilityUseCase.listAbilities(0, 10, "name", "asc"))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("DB error"))
                .verify();
    }

    @Test
    void findByNames_shouldReturnAbilities_whenRepositoryReturnsData() {
        Ability ability = buildAbilityWithTechnologies(List.of(
                new Technology("Java", "1.8"),
                new Technology("Spring", "5.0"),
                new Technology("Docker", "19.03")
        ));

        when(abilityRepository.findByNames(List.of("Java", "Spring")))
                .thenReturn(Flux.just(ability));

        StepVerifier.create(abilityUseCase.findByNames(List.of("Java", "Spring")))
                .expectNext(ability)
                .verifyComplete();
    }

    @Test
    void findByNames_shouldFail_whenRepositoryReturnsEmpty() {
        when(abilityRepository.findByNames(List.of("NoExiste")))
                .thenReturn(Flux.empty());

        StepVerifier.create(abilityUseCase.findByNames(List.of("NoExiste")))
                .expectErrorMatches(ex -> ex instanceof IllegalStateException &&
                        ex.getMessage().contains("No hay capacidades registradas"))
                .verify();
    }

    @Test
    void findByNames_shouldFail_whenRepositoryErrors() {
        when(abilityRepository.findByNames(any()))
                .thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(abilityUseCase.findByNames(List.of("Java")))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("DB error"))
                .verify();
    }


    @Test
    void deleteByIds_shouldSucceed_whenRepositorySucceeds() {
        List<Long> ids = Arrays.asList(1L, 2L);

        when(abilityRepository.deleteByIds(ids)).thenReturn(Mono.empty());

        StepVerifier.create(abilityUseCase.deleteByIds(ids))
                .verifyComplete();

        verify(abilityRepository).deleteByIds(ids);
    }

    @Test
    void deleteByIds_shouldFail_whenRepositoryErrors() {
        List<Long> ids = Arrays.asList(1L, 2L);

        when(abilityRepository.deleteByIds(ids))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(abilityUseCase.deleteByIds(ids))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("DB error"))
                .verify();
    }
}
