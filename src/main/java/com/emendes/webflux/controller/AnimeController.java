package com.emendes.webflux.controller;

import com.emendes.webflux.domain.Anime;
import com.emendes.webflux.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

}
