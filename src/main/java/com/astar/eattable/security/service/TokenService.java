package com.astar.eattable.security.service;

import com.astar.eattable.security.dto.Role;
import com.astar.eattable.security.dto.TokenDTO;
import com.astar.eattable.user.exception.UserNotFoundException;
import com.astar.eattable.user.model.User;
import com.astar.eattable.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.token.expire.seconds}")
    private long accessTokenExpireSeconds;

    @Value("${jwt.refresh.token.expire.seconds}")
    private long refreshTokenExpireSeconds;

    public TokenDTO generateToken(Long userId, Role role) {
        String accessToken = Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpireSeconds * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpireSeconds * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        return new TokenDTO(accessToken, refreshToken);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public User getUserFromToken(String token) {
        Long userId = Long.valueOf(Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
