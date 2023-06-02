package com.emendes.webflux.controller;

import com.emendes.webflux.domain.Anime;
import com.emendes.webflux.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("animes")
@Tag(name = "Anime", description = "Anime management APIs")
@SecurityScheme(
    name = "Basic Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
public class AnimeController {

  private final AnimeService animeService;

  @Operation(
      summary = "List all animes",
      tags = {"Anime"},
      security = {@SecurityRequirement(name = "Basic Authentication")}
  )
  @GetMapping
  public Flux<Anime> listAll() {
    log.info("searching for all animes");
    return animeService.findAll();
  }

  @Operation(
      summary = "Find anime by id",
      tags = {"Anime"},
      security = {@SecurityRequirement(name = "Basic Authentication")}
  )
  @GetMapping("/{id}")
  public Mono<Anime> findById(@PathVariable(name = "id") Integer id) {
    log.info("searching for anime with id: {}", id);
    return animeService.findById(id);
  }

  @Operation(
      summary = "Create Anime",
      tags = {"Anime"},
      security = {@SecurityRequirement(name = "Basic Authentication")}
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Anime> save(@Valid @RequestBody Anime anime) {
    return animeService.save(anime);
  }

  @Operation(
      summary = "Create List of Animes",
      tags = {"Anime"},
      security = {@SecurityRequirement(name = "Basic Authentication")}
  )
  @PostMapping("/batch")
  @ResponseStatus(HttpStatus.CREATED)
  public Flux<Anime> saveBatch(@RequestBody List<Anime> animeList) {
    return animeService.saveAll(animeList);
  }

  @Operation(
      summary = "Update anime by id",
      tags = {"Anime"},
      security = {@SecurityRequirement(name = "Basic Authentication")}
  )
  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> update(@PathVariable(name = "id") int id, @Valid @RequestBody Anime anime) {
    return animeService.update(anime.withId(id));
  }

  @Operation(
      summary = "Delete anime by id",
      tags = {"Anime"},
      security = {@SecurityRequirement(name = "Basic Authentication")}
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<Void> delete(@PathVariable(name = "id") int id) {
    return animeService.delete(id);
  }

}
