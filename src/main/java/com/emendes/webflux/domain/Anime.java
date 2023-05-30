package com.emendes.webflux.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@With
@Table(name = "t_anime")
public class Anime {

  @Id
  private Integer id;
  @NotBlank(message = "name must not be blank")
  private String name;

}
