package com.emendes.webflux.service;

import com.emendes.webflux.domain.Anime;
import com.emendes.webflux.repository.AnimeRepository;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnimeService {

  private final AnimeRepository animeRepository;

  public Flux<Anime> findAll() {
    return animeRepository.findAll();
  }

  public Mono<Anime> findById(Integer id) {
    return animeRepository.findById(id)
        .switchIfEmpty(monoResponseStatusNotFoundException());
  }

  public Mono<Anime> save(Anime anime) {
    return animeRepository.save(anime);
  }

  public Mono<Void> update(Anime anime) {
    return findById(anime.getId())
        .flatMap(animeToBeUpdated -> animeRepository.save(anime))
        .then();
  }

  public Mono<Void> delete(int id) {
    return animeRepository.deleteById(id);
  }

  public <T> Mono<T> monoResponseStatusNotFoundException() {
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
  }

  @Transactional
  public Flux<Anime> saveAll(List<Anime> animeList) {
    return animeRepository.saveAll(animeList)
        .doOnNext(this::throwResponseStatusExceptionWhenEmptyName);
  }

  private void throwResponseStatusExceptionWhenEmptyName(Anime anime) {
    if (StringUtil.isNullOrEmpty(anime.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
    }
  }
}
