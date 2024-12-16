package org.example.service;

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
        return repository.findById(id);
    }

    public void update(Long userId, Integer messageId, UserState newState) {
        AppUser user = findById(userId).orElseThrow();
        user.setLastMessageId(messageId);
        user.setState(newState);
        repository.save(user);
    }

    public void createToken(@NonNull AppUser user, String newToken) {
        Token token = new Token();
        token.setToken(newToken);
        user.setToken(token);
        user.setRole(UserRole.ADMIN);
        repository.save(user);
    }

    public AppUser save(Long userId, String firstName, String lastName) {
        AppUser appUser = new AppUser();
        appUser.setId(userId);
        appUser.setFirstName(firstName);
        appUser.setLastName(Optional.ofNullable(lastName).orElse(""));
        appUser.setLocale("ru");
        appUser.setRole(UserRole.USER);
        return repository.save(appUser);
    }
}
