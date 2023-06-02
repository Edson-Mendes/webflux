package com.emendes.webflux.integration;

import com.emendes.webflux.domain.Anime;
import com.emendes.webflux.repository.AnimeRepository;
import com.emendes.webflux.util.WebTestClientUtil;
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
  private WebTestClientUtil webTestClientUtil;

  @MockBean
  private AnimeRepository animeRepositoryMock;

  private WebTestClient testClientUser;
  private WebTestClient testClientAdmin;
  private WebTestClient testClientNonAuthenticate;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeEach
  public void setUp() {
    testClientUser = webTestClientUtil.authenticateClient("user@email.com", "1234567890");
    testClientAdmin = webTestClientUtil.authenticateClient("admin@email.com", "1234567890");
    testClientNonAuthenticate = webTestClientUtil.authenticateClient("", "");

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
  void listAll_ReturnsFluxOfAnime_WhenUserHaveRoleUser() {
    testClientUser
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
  void listAll_ReturnsFluxOfAnime_WhenUserHaveRoleAdmin() {
    testClientAdmin
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
    testClientNonAuthenticate
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("listAll returns a flux of Anime")
  void listAll_Flavor2_ReturnsFluxOfAnime_WhenSuccessful() {
    testClientUser
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Anime.class)
        .hasSize(1).contains(anime);
  }

  @Test
  @DisplayName("findById returns a mono of Anime when it exists and user have role USER")
  void findById_ReturnsMonoOfAnime_WhenItExistsAndUserHaveRoleUser() {
    testClientUser
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("findById returns a mono of Anime when it exists and user have role ADMIN")
  void findById_ReturnsMonoOfAnime_WhenItExistsAndUserHaveRoleAdmin() {
    testClientAdmin
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
    testClientNonAuthenticate
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("findById returns a mono Error when anime does not exist and user is authenticate and have role USER")
  void findById_ReturnsMonoError_WhenAnimeDoesNotExist() {
    testClientUser
        .get()
        .uri("/animes/{id}", 100)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }

  @Test
  @DisplayName("save create Anime when create successful and user have role ADMIN")
  void save_CreateAnime_WhenCreateSuccessfulAndUserHaveRoleAdmin() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    testClientAdmin
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
  void save_ReturnsForbidden_UserHaveRoleUser() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    testClientUser
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

    testClientNonAuthenticate
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("save returns error when name is empty and user is authenticate and have role ADMIN")
  void save_ReturnsError_WhenNameIsEmpty() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved().withName("");

    testClientAdmin
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
  void saveBatch_CreatesListOfAnime_WhenCreateSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    testClientAdmin
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
  void saveBatch_ReturnsError_WhenOneAnimeOnTheListContainsInvalidFields() {
    BDDMockito.when(animeRepositoryMock.saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved().withName(""))))
        .thenReturn(Flux.just(anime, anime.withName("")));

    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    testClientAdmin
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
  void delete_ReturnsNoContent_WhenDeleteSuccessful() {
    testClientAdmin
        .delete()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @DisplayName("delete returns forbidden when user have role USER")
  void delete_ReturnsForbidden_WhenUserHaveRoleUser() {
    testClientUser
        .delete()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("update returns no content when update successful and user is authenticate and have role ADMIN")
  void update_ReturnsNoContent_WhenUpdateSuccessful() {
    Anime animeToBeUpdated = AnimeCreator.createValidUpdatedAnime();

    testClientAdmin
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeUpdated))
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @DisplayName("update returns Error when Anime does not exist and user is authenticate and have role ADMIN")
  void update_ReturnsError_WhenAnimeDoesNotExist() {
    Anime animeToBeUpdated = AnimeCreator.createValidUpdatedAnime();

    testClientAdmin
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
  void update_ReturnsMonoError_WhenNameIsEmpty() {
    Anime animeToBeUpdated = AnimeCreator.createValidUpdatedAnime().withName("");

    testClientAdmin
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
