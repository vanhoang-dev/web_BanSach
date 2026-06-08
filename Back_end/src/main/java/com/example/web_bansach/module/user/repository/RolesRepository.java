package com.example.web_bansach.module.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.user.entity.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    Roles findByName(String name);
}



