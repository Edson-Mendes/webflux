package com.emendes.webflux.integration;

import com.emendes.webflux.config.ResourceWebPropertiesConfig;
import com.emendes.webflux.domain.Anime;
import com.emendes.webflux.repository.AnimeRepository;
import com.emendes.webflux.service.AnimeService;
import com.emendes.webflux.util.creator.AnimeCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import({AnimeService.class, ResourceWebPropertiesConfig.class})
class AnimeControllerIT {

  @MockBean
  private AnimeRepository animeRepositoryMock;

  @Autowired
  private WebTestClient testClient;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeEach
  public void setUp() {
    BDDMockito.when(animeRepositoryMock.findAll()).thenReturn(Flux.just(anime));

    BDDMockito.when(animeRepositoryMock.findById(1)).thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepositoryMock.findById(100)).thenReturn(Mono.empty());
//
//    BDDMockito.when(animeRepositoryMock.save(any(Anime.class)))
//        .thenReturn(Mono.just(anime));
//
//    BDDMockito.when(animeRepositoryMock.deleteById(anyInt()))
//        .thenReturn(Mono.empty());
  }

  @Test
  @DisplayName("listAll returns a flux of Anime")
  void listAll_ReturnsFluxOfAnime_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.[0].id").isEqualTo(anime.getId())
        .jsonPath("$.[0].name").isEqualTo(anime.getName());
  }

  @Test
  @DisplayName("listAll returns a flux of Anime")
  void listAll_Flavor2_ReturnsFluxOfAnime_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Anime.class)
        .hasSize(1).contains(anime);
  }

  @Test
  @DisplayName("findById returns a mono of Anime when it exists")
  void findById_ReturnsMonoOfAnime_WhenItExists() {
    testClient
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("findById returns a mono Error when anime does not exist")
  void findById_ReturnsMonoError_WhenAnimeDoesNotExist() {
    testClient
        .get()
        .uri("/animes/{id}", 100)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }

}
