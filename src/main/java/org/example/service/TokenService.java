package org.example.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.entity.Token;
import org.example.repository.TokenRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final TokenRepository repository;

  public Optional<Token> findByToken(String token) {
    return repository.findByToken(token);
  }

  public @NonNull Token save(String newToken) {
    Token token = new Token();
    token.setToken(newToken);
    return repository.save(token);
  }
}
