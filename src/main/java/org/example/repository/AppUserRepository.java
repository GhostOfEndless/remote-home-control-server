package org.example.repository;

import java.util.List;
import java.util.Optional;
import org.example.entity.AppUser;
import org.example.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.token WHERE u.id = :id")
  Optional<AppUser> findByIdWithToken(Long id);

  @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.token WHERE u.username = :username")
  Optional<AppUser> findByUsername(String username);

  @Query("SELECT u FROM AppUser u WHERE u.token = :token AND u.id != :currentUserId")
  List<AppUser> findAllByTokenWithoutCurrent(Long currentUserId, Token token);
}
