package com.shirtcompany.auth.service;

import com.shirtcompany.auth.model.User;
import com.shirtcompany.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public Mono<User> register(User user) {
        // Aquí se podría encriptar el password y validar datos
        return userRepository.save(user);
    }

    public Mono<String> login(User user) {
        // Lógica dummy para login
        return userRepository.findByUsername(user.getUsername())
                .filter(u -> u.getPassword().equals(user.getPassword()))
                .map(u -> "Login exitoso")
                .switchIfEmpty(Mono.just("Credenciales inválidas"));
    }

    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setPassword(updatedUser.getPassword());
                    return userRepository.save(existingUser);
                });
    }

    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }
}
