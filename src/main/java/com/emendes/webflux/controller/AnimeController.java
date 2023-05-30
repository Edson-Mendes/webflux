package com.emendes.webflux.controller;

import com.emendes.webflux.domain.Anime;
import com.emendes.webflux.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("animes")
public class AnimeController {

  private final AnimeRepository animeRepository;

  @GetMapping
  public Flux<Anime> listAll() {
    log.info("searching for all animes");
    return animeRepository.findAll();
  }

}
