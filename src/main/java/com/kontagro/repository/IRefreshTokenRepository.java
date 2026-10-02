package com.kontagro.repository;

import com.kontagro.entities.RefreshToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @EntityGraph(attributePaths = "usuario")
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByUsuario_Id(Long idUsuario);
}
