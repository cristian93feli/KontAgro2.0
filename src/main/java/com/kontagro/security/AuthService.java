package com.kontagro.security;

import com.kontagro.entities.RefreshToken;
import com.kontagro.entities.Usuario;
import com.kontagro.exceptions.UnauthorizedException;
import com.kontagro.repository.IRefreshTokenRepository;
import com.kontagro.repository.IUsuarioRepository;
import com.kontagro.utils.MensajesError;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Service
public class AuthService implements UserDetailsService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long expiration;

    @Value("${application.security.refresh-token.idle-expiration}")
    private long refreshIdleExpiration;

    @Value("${application.security.refresh-token.absolute-expiration}")
    private long refreshAbsoluteExpiration;

    private final IUsuarioRepository usuarioRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(IUsuarioRepository usuarioRepository, IRefreshTokenRepository refreshTokenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Usuario usuario) {
        return generateToken(usuario, System.currentTimeMillis() + expiration);
    }

    public String generateToken(Usuario usuario, long expiresAt) {
        return Jwts.builder()
                .subject(usuario.getUsuario())
                .issuedAt(new Date())
                .expiration(new Date(expiresAt))
                .signWith(getSigningKey())
                .compact();
    }

    public long getAccessTokenExpirationMillis() {
        return expiration;
    }

    @Transactional
    public String createRefreshToken(Usuario usuario) {
        String rawToken = generarTokenAleatorio();
        LocalDateTime ahora = LocalDateTime.now();

        RefreshToken token = new RefreshToken();
        token.setUsuario(usuario);
        token.setTokenHash(hash(rawToken));
        token.setFechaCreacion(ahora);
        token.setUltimaActividad(ahora);
        token.setFechaExpiracionAbsoluta(ahora.plus(Duration.ofMillis(refreshAbsoluteExpiration)));
        token.setRevocado(false);
        refreshTokenRepository.save(token);
        return rawToken;
    }

    @Transactional
    public Usuario validateAndTouchRefreshToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new UnauthorizedException("La sesión no puede renovarse porque no existe un token de renovación válido.");
        }

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new UnauthorizedException("La sesión ya no puede renovarse. Inicie sesión nuevamente."));

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limiteInactividad = token.getUltimaActividad().plus(Duration.ofMillis(refreshIdleExpiration));
        if (token.isRevocado() || ahora.isAfter(token.getFechaExpiracionAbsoluta()) || ahora.isAfter(limiteInactividad)) {
            token.setRevocado(true);
            refreshTokenRepository.save(token);
            throw new UnauthorizedException("La sesión expiró por inactividad o por alcanzar su duración máxima. Inicie sesión nuevamente.");
        }

        token.setUltimaActividad(ahora);
        refreshTokenRepository.save(token);
        return token.getUsuario();
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        refreshTokenRepository.findByTokenHash(hash(rawToken)).ifPresent(token -> {
            token.setRevocado(true);
            refreshTokenRepository.save(token);
        });
    }

    public String getUsuario(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException(MensajesError.USUARIO_NO_EXISTE));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getUsuario())
                .password(usuario.getContrasena())
                .authorities("USER")
                .build();
    }

    private String generarTokenAleatorio() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No fue posible inicializar el hash de seguridad.", ex);
        }
    }
}
