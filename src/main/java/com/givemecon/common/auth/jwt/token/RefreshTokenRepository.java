package com.givemecon.common.auth.jwt.token;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByMemberId(String memberId);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
