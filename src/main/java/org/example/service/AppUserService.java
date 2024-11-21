package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.AppUser;
import org.example.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository repository;

    public Optional<AppUser> findById(Long id) {
        return repository.findById(id);
    }
}
