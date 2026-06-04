package org.example.ecommerceapi.domain.user.repository;

import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}