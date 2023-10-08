//package com.example.stock.security;
//
//import io.jsonwebtoken.Claims;
//import lombok.RequiredArgsConstructor;
//import lombok.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class TokenProvider {
//
//    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;
//    private static final String KEY_ROLES = "roles";
//
//    @Value("{spring.jwt.secret}")
//    private String secretKey;
//
//    public String generatedToken(String username, List<String> roles) {
//        Claims claims = Jwts.claims().setSubject(username);
//        claims.put("roles", roles);
//
//        var now = new Date();
//        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);
//
//        Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiredDate)
//                .signWith(SignatureAlgorithm.HS512)
//        return null;
//    }
//}
