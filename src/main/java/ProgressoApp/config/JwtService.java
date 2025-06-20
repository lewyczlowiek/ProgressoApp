package ProgressoApp.config;

import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final SecretKey accessSecretKey;
  private final Integer accessTokenValidityInMin;
  private final SecretKey refreshSecretKey;
  private final Integer refreshTokenValidityInMin;

  public JwtService(@Value("${jwt.access-secret-key}") String accessSecretKey,
      @Value("${jwt.access-token-validity-in-min}") Integer accessTokenValidityInMin,
      @Value("${jwt.refresh-secret-key}") String refreshSecretKey,
      @Value("${jwt.refresh-token-validity-in-min}") Integer refreshTokenValidityInMin) {
    System.out.println("accessSecretKey = " + accessSecretKey); // for debugging
    this.accessSecretKey = Keys.hmacShaKeyFor(accessSecretKey.getBytes(StandardCharsets.UTF_8));
    this.accessTokenValidityInMin = accessTokenValidityInMin;
    this.refreshSecretKey = Keys.hmacShaKeyFor(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
    this.refreshTokenValidityInMin = refreshTokenValidityInMin;
  }

  private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails,
      SecretKey secretKey, Integer tokenValidityInMin) {
    final Instant now = Instant.now();
    final Instant expiration = now.plus(tokenValidityInMin, ChronoUnit.MINUTES);
    final var authorities = userDetails.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .toList();
    return Jwts.builder()
        .addClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(expiration))
        .claim("authorities", authorities)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims extractAllClaims(String token, SecretKey secretKey) {

    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private <T> T extractClaim(String token, SecretKey secretKey,
      Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token, secretKey);
    return claimsResolver.apply(claims);
  }

  private String extractUserName(String token, SecretKey secretKey) {
    return extractClaim(token, secretKey, Claims::getSubject);
  }

  public String extractUserNameFromAccessToken(String token) {
    return extractUserName(token, this.accessSecretKey);
  }

  public String extractUserNameFromRefrewshToken(String token) {
    return extractUserName(token, this.refreshSecretKey);
  }

  private boolean isTokenValid(String token, SecretKey secretKey, UserDetails userDetails) {
    final Claims claims = extractAllClaims(token, secretKey);
    final Date expiration = claims.getExpiration();
    final String userName = claims.getSubject();
    boolean valid = !expiration.before(Date.from(Instant.now()));
    return userName.equals(userDetails.getUsername()) && valid;
  }

  public boolean isAccessTokenValid(String token, UserDetails userDetails) {
    return isTokenValid(token, this.accessSecretKey, userDetails);
  }

  public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
    return isTokenValid(token, this.refreshSecretKey, userDetails);
  }

  public String generateAccessToken(UserDetails userDetails) {
    return generateAccessToken(Map.of(), userDetails);
  }

  public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return generateToken(extraClaims, userDetails,
        this.accessSecretKey, this.accessTokenValidityInMin);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return generateRefreshToken(Map.of(), userDetails);
  }

  public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return generateToken(extraClaims, userDetails,
        this.refreshSecretKey, this.refreshTokenValidityInMin);
  }
}