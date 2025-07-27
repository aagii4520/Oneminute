package com.oneminute.auth.repository;

import com.oneminute.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); //used for login

    boolean existsByEmail(String email); // used during register
}
