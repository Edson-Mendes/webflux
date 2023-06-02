package com.emendes.webflux.integration;

import com.emendes.webflux.domain.Anime;
import com.emendes.webflux.repository.AnimeRepository;
import com.emendes.webflux.util.creator.AnimeCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
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

  @Autowired
  private WebTestClient client;

  @MockBean
  private AnimeRepository animeRepositoryMock;

  private final Anime anime = AnimeCreator.createValidAnime();

  private static final String REGULAR_USER = "user@email.com";
  private static final String ADMIN_USER = "admin@email.com";

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
  @DisplayName("listAll returns a flux of Anime when user have role USER")
  @WithUserDetails(REGULAR_USER)
  void listAll_ReturnsFluxOfAnime_WhenUserHaveRoleUser() {
    client
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.[0].id").isEqualTo(anime.getId())
        .jsonPath("$.[0].name").isEqualTo(anime.getName());
  }

  @Test
  @DisplayName("listAll returns a flux of Anime when user have role ADMIN")
  @WithUserDetails(ADMIN_USER)
  void listAll_ReturnsFluxOfAnime_WhenUserHaveRoleAdmin() {
    client
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.[0].id").isEqualTo(anime.getId())
        .jsonPath("$.[0].name").isEqualTo(anime.getName());
  }

  @Test
  @DisplayName("listAll returns unauthorized when user is  not authenticate")
  void listAll_ReturnsUnauthorized_WhenUserIsNotAuthenticate() {
    client
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("listAll returns a flux of Anime")
  @WithUserDetails(REGULAR_USER)
  void listAll_Flavor2_ReturnsFluxOfAnime_WhenSuccessful() {
    client
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Anime.class)
        .hasSize(1).contains(anime);
  }

  @Test
  @DisplayName("findById returns a mono of Anime when it exists and user have role USER")
  @WithUserDetails(REGULAR_USER)
  void findById_ReturnsMonoOfAnime_WhenItExistsAndUserHaveRoleUser() {
    client
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("findById returns a mono of Anime when it exists and user have role ADMIN")
  @WithUserDetails(ADMIN_USER)
  void findById_ReturnsMonoOfAnime_WhenItExistsAndUserHaveRoleAdmin() {
    client
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("findById returns unauthorized when user is not authenticate")
  void findById_ReturnsUnauthorized_WhenUserIsNotAuthenticate() {
    client
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("findById returns a mono Error when anime does not exist and user is authenticate and have role USER")
  @WithUserDetails(REGULAR_USER)
  void findById_ReturnsMonoError_WhenAnimeDoesNotExist() {
    client
        .get()
        .uri("/animes/{id}", 100)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }

  @Test
  @DisplayName("save create Anime when create successful and user have role ADMIN")
  @WithUserDetails(ADMIN_USER)
  void save_CreateAnime_WhenCreateSuccessfulAndUserHaveRoleAdmin() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    client
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
  @DisplayName("save returns forbidden when user have role USER")
  @WithUserDetails(REGULAR_USER)
  void save_ReturnsForbidden_UserHaveRoleUser() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    client
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("save returns unauthorized when user is not authenticate")
  void save_ReturnsUnauthorized_WhenUserIsNotAuthenticate() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    client
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("save returns error when name is empty and user is authenticate and have role ADMIN")
  @WithUserDetails(ADMIN_USER)
  void save_ReturnsError_WhenNameIsEmpty() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved().withName("");

    client
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
  @DisplayName("saveBatch creates a List of Anime when create successful and user is authenticate and have role ADMIN")
  @WithUserDetails(ADMIN_USER)
  void saveBatch_CreatesListOfAnime_WhenCreateSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    client
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
  @DisplayName("saveBatch returns Error when one anime on the list contains invalid fields and user is authenticate and have role ADMIN")
  @WithUserDetails(ADMIN_USER)
  void saveBatch_ReturnsError_WhenOneAnimeOnTheListContainsInvalidFields() {
    BDDMockito.when(animeRepositoryMock.saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved().withName(""))))
        .thenReturn(Flux.just(anime, anime.withName("")));

    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    client
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
  @DisplayName("delete returns no content when delete successful and user is authenticate and have role ADMIN")
  @WithUserDetails(ADMIN_USER)
  void delete_ReturnsNoContent_WhenDeleteSuccessful() {
    client
        .delete()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @DisplayName("delete returns forbidden when user have role USER")
  @WithUserDetails(REGULAR_USER)
  void delete_ReturnsForbidden_WhenUserHaveRoleUser() {
    client
        .delete()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("update returns no content when update successful and user is authenticate and have role ADMIN")
  @WithUserDetails(ADMIN_USER)
  void update_ReturnsNoContent_WhenUpdateSuccessful() {
    Anime animeToBeUpdated = AnimeCreator.createValidUpdatedAnime();

    client
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeUpdated))
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @DisplayName("update returns Error when Anime does not exist and user is authenticate and have role ADMIN")
  @WithUserDetails(ADMIN_USER)
  void update_ReturnsError_WhenAnimeDoesNotExist() {
    Anime animeToBeUpdated = AnimeCreator.createValidUpdatedAnime();

    client
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
  @DisplayName("update returns a mono Error when Name is empty and user is authenticate and have role ADMIN")
  @WithUserDetails(ADMIN_USER)
  void update_ReturnsMonoError_WhenNameIsEmpty() {
    Anime animeToBeUpdated = AnimeCreator.createValidUpdatedAnime().withName("");

    client
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
