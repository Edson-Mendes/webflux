package com.emendes.webflux.controller;

import com.emendes.webflux.domain.Anime;
import com.emendes.webflux.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("animes")
public class AnimeController {

  private final AnimeService animeService;

  @GetMapping
  public Flux<Anime> listAll() {
    log.info("searching for all animes");
    return animeService.findAll();
  }

  @GetMapping("/{id}")
  public Mono<Anime> findById(@PathVariable(name = "id") Integer id) {
    log.info("searching for anime with id: {}", id);
    return animeService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Anime> save(@Valid @RequestBody Anime anime) {
    return animeService.save(anime);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> update(@PathVariable(name = "id") int id, @Valid @RequestBody Anime anime) {
    return animeService.update(anime.withId(id));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> delete(@PathVariable(name = "id") int id) {
    return animeService.delete(id);
  }

}
