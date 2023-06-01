package com.emendes.webflux.unit.controller;

import com.emendes.webflux.controller.AnimeController;
import com.emendes.webflux.domain.Anime;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

  @InjectMocks
  private AnimeController animeController;
  @Mock
  private AnimeService animeServiceMock;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeEach
  public void setUp() {
    BDDMockito.when(animeServiceMock.findAll()).thenReturn(Flux.just(anime));

    BDDMockito.when(animeServiceMock.findById(1)).thenReturn(Mono.just(anime));

    BDDMockito.when(animeServiceMock.save(any(Anime.class)))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeServiceMock.delete(anyInt()))
        .thenReturn(Mono.empty());
  }

  @Test
  @DisplayName("listAll returns a flux of Anime")
  void listAll_ReturnsFluxOfAnime_WhenSuccessful() {
    StepVerifier.create(animeController.listAll())
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns a mono of Anime when it exists")
  void findById_ReturnsMonoOfAnime_WhenItExists() {
    StepVerifier.create(animeController.findById(1))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("save returns a mono of Anime when create successful")
  void save_ReturnsMonoOfAnime_WhenCreateSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    StepVerifier.create(animeController.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("delete returns a mono of Void when delete successful")
  void delete_ReturnsMonoOfVoid_WhenDeleteSuccessful() {
    StepVerifier.create(animeController.delete(1))
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  @DisplayName("update returns a mono of Void when update successful")
  void update_ReturnsMonoOfAnime_WhenUpdateSuccessful() {
    BDDMockito.when(animeServiceMock.update(any(Anime.class)))
        .thenReturn(Mono.empty());

    Anime animeToBeUpdated = AnimeCreator.createValidAnime();

    StepVerifier.create(animeController.update(1, animeToBeUpdated))
        .expectSubscription()
        .verifyComplete();
  }

}