package com.test.globalkinetik.config.jwt;

import com.test.globalkinetik.model.Users;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.lang.String.format;

/**
 * @author S'phokuhle on 9/13/2021
 */
@Component
@Slf4j
public class JwtTokenUtil {

    @Value("${app.security.jwtSecret}")
    private String jwtSecret;

    @Value("${app.security.jwtIssuer}")
    private String jwtIssuer;

    @Value("${app.security.jwtExpiryTimeMillis}")
    private long jwtExpiryTimeInMillis;


    /**
     * Takes the user information and put the id and username in the subject of the token and generates the token
     * @param user
     * @return
     */
    public String generateAccessToken(Users user) {
        return Jwts.builder()
                .setSubject(format("%s,%s", user.getId(), user.getUsername()))
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiryTimeInMillis)) // 3 minutes
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Get the user id from the token
     * @param token
     * @return
     */
    public String getUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token.replaceFirst("Bearer ", ""))
                .getBody();
        return claims.getSubject().split(",")[0];
    }

    /**
     * Get the username from the token
     * @param token
     * @return
     */
    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token.replaceFirst("Bearer ", ""))
                .getBody();
        return claims.getSubject().split(",")[1];
    }

    /**
     * Expires the token, this method is called when logging out
     * @param token
     * @return return the expired token.
     */
    public String invalidateToken(String token) {
        String username = getUsername(token);
        String userId = getUserId(token);
        return Jwts.builder()
                .setSubject(format("%s,%s", userId, username))
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Takes the token and validate it against our secret key
     * @param token
     * @return true if the token is valid and false if the token is incorrect or expired
     */
    public boolean validate(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature - {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty - {}", ex.getMessage());
        }
        return false;
    }
}
