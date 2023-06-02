package com.emendes.webflux.repository;

import com.emendes.webflux.domain.DevDojoUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<DevDojoUser, Integer> {

  Mono<DevDojoUser> findByUsername(String username);

}
