package org.example.repository;

import java.util.Optional;
import org.example.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

  Optional<Token> findByToken(String token);
}
