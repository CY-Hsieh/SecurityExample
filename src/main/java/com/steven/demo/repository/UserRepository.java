package com.steven.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.steven.demo.model.User;



public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUserName(String name);
}
