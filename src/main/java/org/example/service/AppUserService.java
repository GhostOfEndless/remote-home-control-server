package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.entity.AppUser;
import org.example.entity.Token;
import org.example.repository.AppUserRepository;
import org.example.service.enums.UserState;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository repository;

    public Optional<AppUser> findById(Long id) {
        return repository.findByIdWithToken(id);
    }

    public List<AppUser> findAllByTokenWithoutCurrent(Long currentUserId, Token token) {
        return repository.findAllByTokenWithoutCurrent(currentUserId, token);
    }

    public Optional<AppUser> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public void update(@NonNull AppUser appUser, Integer messageId, UserState newState) {
        appUser.setLastMessageId(messageId);
        appUser.setState(newState);
        repository.save(appUser);
    }

    public void update(@NonNull AppUser appUser, Token token, UserRole userRole) {
        appUser.setToken(token);
        appUser.setRole(userRole);
        repository.save(appUser);
    }

    public void update(@NonNull AppUser appUser, Token token) {
        appUser.setToken(token);
        repository.save(appUser);
    }

    public void removeTokenFromUser(@NonNull Long userId) {
        AppUser appUser = findById(userId).orElseThrow();
        appUser.setToken(null);
        repository.save(appUser);
    }

    public AppUser save(Long userId, String firstName, String lastName, String username) {
        AppUser appUser = new AppUser();
        appUser.setId(userId);
        appUser.setFirstName(firstName);
        appUser.setLastName(Optional.ofNullable(lastName).orElse(""));
        appUser.setUsername(username);
        appUser.setLocale("ru");
        appUser.setRole(UserRole.USER);
        return repository.save(appUser);
    }
}
