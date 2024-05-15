package com.example.arjon.repository;

import com.example.arjon.model.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
}
