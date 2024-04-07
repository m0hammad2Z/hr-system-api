package org.hrsys.util.security;

import org.hrsys.exception.InvalidTokenException;
import org.hrsys.util.Constants;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET = Constants.SECRET;

    // Generate token with email, expiration time, and role
    public static String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Constants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // Get email from token
    public static String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // Get role from token
    public static String getRole(String token) {
        return (String) parseClaims(token).get("role");
    }

    // Validate token - combines checks and throws specific exceptions
    public static void validateToken(String token) throws InvalidTokenException {
        try {
            Claims claims = parseClaims(token);
            validateClaimPresence(claims);
            validateRole(getRole(token));
            validateExpiration(claims.getExpiration());
        } catch (SignatureException e) {
            throw new InvalidTokenException("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("Unsupported JWT token");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Invalid JWT token");
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("JWT claims string is empty");
        }
    }

    // Resolve token from header
    public static String resolveToken(String header) {
        return header.replace("Bearer ", "");
    }

    // Helper method to parse claims with consistent secret key
    private static Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

    // Helper method to validate claim presence
    private static void validateClaimPresence(Claims claims) throws InvalidTokenException {
        if (claims.getSubject() == null) {
            throw new InvalidTokenException("Invalid JWT token (missing claim: " + "email" + ")");
        }
        if (claims.get("role") == null) {
            throw new InvalidTokenException("Invalid JWT token (missing claim: " + "role" + ")");
        }
    }

    // Helper method to validate role
    private static void validateRole(String role) throws InvalidTokenException {
        if (!role.equals("ROLE_MANAGER") && !role.equals("ROLE_EMPLOYEE")) {
            throw new InvalidTokenException("Invalid JWT token (invalid role)");
        }
    }

    // Helper method to validate expiration
    private static void validateExpiration(Date expiration) throws InvalidTokenException {
        if (expiration.before(new Date())) {
            throw new InvalidTokenException("Expired JWT token");
        }
    }
}

