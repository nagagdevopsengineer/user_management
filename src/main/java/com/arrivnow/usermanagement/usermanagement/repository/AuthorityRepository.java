package com.arrivnow.usermanagement.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arrivnow.usermanagement.usermanagement.model.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String>{

}
