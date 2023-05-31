package com.emendes.webflux.util.creator;

import com.emendes.webflux.domain.Anime;

public class AnimeCreator {

  private AnimeCreator(){}

  public static Anime createAnimeToBeSaved() {
    return Anime.builder()
        .name("Tensei Shitara Slime Datta Ken")
        .build();
  }

  public static Anime createValidAnime() {
    return Anime.builder()
        .id(1)
        .name("Tensei Shitara Slime Datta Ken")
        .build();
  }

  public static Anime createValidUpdatedAnime() {
    return Anime.builder()
        .id(1)
        .name("Tensei Shitara Slime Datta Ken 2")
        .build();
  }

}
