package com.shirtcompany.auth.controller;

import com.shirtcompany.auth.model.User;
import com.shirtcompany.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Mono<User> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody User user) {
        return authService.login(user);
    }

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable Long id) {
        return authService.getUserById(id);
    }

    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return authService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return authService.deleteUser(id);
    }
}