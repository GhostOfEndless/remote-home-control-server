package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.AppUser;
import org.example.repository.AppUserRepository;
import org.example.service.enums.UserState;
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

    public AppUser save(Long userId) {
        AppUser appUser = new AppUser();
        appUser.setId(userId);
        return repository.save(appUser);
    }
}
