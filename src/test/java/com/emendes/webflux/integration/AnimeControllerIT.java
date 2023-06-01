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
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
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

    BDDMockito.when(animeRepositoryMock.save(any(Anime.class)))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepositoryMock.deleteById(anyInt()))
        .thenReturn(Mono.empty());

    BDDMockito.when(animeRepositoryMock.saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved())))
        .thenReturn(Flux.just(anime, anime));
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

  @Test
  @DisplayName("save create Anime when create successful")
  void save_CreateAnime_WhenCreateSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("save returns error when name is empty")
  void save_ReturnsError_WhenNameIsEmpty() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved().withName("");

    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400);
  }

  @Test
  @DisplayName("saveBatch creates a List of Anime when create successful")
  void saveBatch_CreatesListOfAnime_WhenCreateSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved)))
        .exchange()
        .expectStatus().isCreated()
        .expectBodyList(Anime.class)
        .hasSize(2).contains(anime);
  }

  @Test
  @DisplayName("saveBatch returns Error when one anime on the list contains invalid fields")
  void saveBatch_ReturnsError_WhenOneAnimeOnTheListContainsInvalidFields() {
    BDDMockito.when(animeRepositoryMock.saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved().withName(""))))
        .thenReturn(Flux.just(anime, anime.withName("")));

    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved.withName(""))))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400);
  }

  @Test
  @DisplayName("delete returns no content when delete successful")
  void delete_ReturnsNoContent_WhenDeleteSuccessful() {
    testClient
        .delete()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @DisplayName("update returns no content when update successful")
  void update_ReturnsNoContent_WhenUpdateSuccessful() {
    Anime animeToBeUpdated = AnimeCreator.createValidUpdatedAnime();

    testClient
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeUpdated))
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @DisplayName("update returns Error when Anime does not exist")
  void update_ReturnsError_WhenAnimeDoesNotExist() {
    Anime animeToBeUpdated = AnimeCreator.createValidUpdatedAnime();

    testClient
        .put()
        .uri("/animes/{id}", 100)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeUpdated))
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }

  @Test
  @DisplayName("update returns a mono Error when Name is empty")
  void update_ReturnsMonoError_WhenNameIsEmpty() {
    Anime animeToBeUpdated = AnimeCreator.createValidUpdatedAnime().withName("");

    testClient
        .put()
        .uri("/animes/{id}", 100)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeUpdated))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400);
  }

}
