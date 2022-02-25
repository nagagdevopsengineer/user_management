package com.arrivnow.usermanagement.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arrivnow.usermanagement.usermanagement.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findOneByLogin(String login);

	Optional<User> findOneByEmailIgnoreCase(String email);

}
