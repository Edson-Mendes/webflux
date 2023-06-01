package com.emendes.webflux.unit.service;

import com.emendes.webflux.domain.Anime;
import com.emendes.webflux.repository.AnimeRepository;
import com.emendes.webflux.service.AnimeService;
import com.emendes.webflux.util.creator.AnimeCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

  @InjectMocks
  private AnimeService animeService;
  @Mock
  private AnimeRepository animeRepositoryMock;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeEach
  public void setUp() {
    BDDMockito.when(animeRepositoryMock.findAll()).thenReturn(Flux.just(anime));

    BDDMockito.when(animeRepositoryMock.findById(1)).thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepositoryMock.findById(100)).thenReturn(Mono.empty());

    BDDMockito.when(animeRepositoryMock.save(any(Anime.class)))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepositoryMock.saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved())))
        .thenReturn(Flux.just(anime, anime));

    BDDMockito.when(animeRepositoryMock.deleteById(anyInt()))
        .thenReturn(Mono.empty());
  }

  @Test
  @DisplayName("findAll returns a flux of Anime")
  void findAll_ReturnsFluxOfAnime_WhenSuccessful() {
    StepVerifier.create(animeService.findAll())
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns a mono of Anime when it exists")
  void findById_ReturnsMonoOfAnime_WhenItExists() {
    StepVerifier.create(animeService.findById(1))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns a mono Error when anime does not exist")
  void findById_ReturnsMonoError_WhenAnimeDoesNotExist() {
    StepVerifier.create(animeService.findById(100))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("save returns a mono of Anime when create successful")
  void save_ReturnsMonoOfAnime_WhenCreateSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    StepVerifier.create(animeService.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("saveAll returns Flux of Anime when create successful")
  void saveAll_ReturnsFluxOfAnime_WhenCreateSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    StepVerifier.create(animeService.saveAll(List.of(animeToBeSaved, animeToBeSaved)))
        .expectSubscription()
        .expectNext(anime, anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("saveAll returns Mono Error when anime on the list contains invalid fields")
  void saveAll_ReturnsMonoError_WhenAnimeOnTheListContainsInvalidFields() {
    BDDMockito.when(animeRepositoryMock.saveAll(anyIterable()))
        .thenReturn(Flux.just(anime, anime.withName("")));

    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    StepVerifier.create(animeService.saveAll(List.of(animeToBeSaved, animeToBeSaved.withName(""))))
        .expectSubscription()
        .expectNext(anime)
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("delete returns a mono of Void when delete successful")
  void delete_ReturnsMonoOfVoid_WhenDeleteSuccessful() {
    StepVerifier.create(animeService.delete(1))
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  @DisplayName("update returns a mono of Void when update successful")
  void update_ReturnsMonoOfAnime_WhenUpdateSuccessful() {
    BDDMockito.when(animeRepositoryMock.save(any(Anime.class)))
        .thenReturn(Mono.just(AnimeCreator.createValidUpdatedAnime()));

    Anime animeToBeUpdated = AnimeCreator.createValidAnime();

    StepVerifier.create(animeService.update(animeToBeUpdated))
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  @DisplayName("update returns a mono Error when Anime does not exist")
  void update_ReturnsMonoError_WhenAnimeDoesNotExist() {
    BDDMockito.when(animeRepositoryMock.findById(anyInt())).thenReturn(Mono.empty());

    Anime animeToBeUpdated = AnimeCreator.createValidAnime();

    StepVerifier.create(animeService.update(animeToBeUpdated))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }

}