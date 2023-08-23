package com.OrderlyAPI.Authorization.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.function.Function;
import java.util.*;

@Service
public class JwtService {
    private final static String SECRET_KEY = "77417A244326464A4054635266556A586E32727538782F413F442A472D4B";

    /** Utility class for JWT token authentication */
    public String generateToken(UserDetails userDetails) {
        /*
        final Integer customerId = ((CustomerModel) userDetails).getId();
        final HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customerId", customerId);
        return generateToken(extraClaims, userDetails);
        */
        return generateToken(new HashMap<String, Object>(), userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String JWTToken, Function<Claims, T> claimsResolver) {
        // Function<InputObj, OutputObj>
        // is an interface that applies a transformation to an object
        // similar to an anonymous function -> invoke this transformation using .apply()
        final Claims claim = extractAllClaims(JWTToken);
        return claimsResolver.apply(claim);
    }

    private Claims extractAllClaims(String JWTtoken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(JWTtoken)
                .getBody();
    }

    public String extractUsername(String JWTtoken) {
        return extractClaim(JWTtoken, Claims::getSubject);
    }

    private Date extractExpiration(String JWTtoken) {
        return extractClaim(JWTtoken, Claims::getExpiration);
    }
    public boolean isTokenValid(String JWTtoken, UserDetails userDetails) {
        final String username = extractUsername(JWTtoken);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(JWTtoken);
    }

    private boolean isTokenExpired(String JWTtoken) {
        return extractExpiration(JWTtoken).before(new Date());
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
