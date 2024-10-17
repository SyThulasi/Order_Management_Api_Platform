package org.example.zerobeta.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "49f3523913e6be15b92d217569837dabf133b16c73cd522b8b8a6b00330493063490eed27c6b0e2ab43eedf1f36951c46ebfd68c116eef33ad0725dd816ca789952e5b70633a14c72bbe55eac6d6a0b707737dd6edff1c44b3e0a408b8fdb5e630a310fa25cc9b5329896ce2bc2a5e47030f311effc121ebdf0ac1d8d14ee705754dabc198a7178ac69e4bc6c507b1540939d28b3baf6b8220964f97be0778e58f905ecd4a10cd1d4891f7ba1fd897d30e3a52c60f9b9c6703209055963d4b4b604e86a2c2bc63e97d1ec79e59144abe0094a99ec348a2f8e21793ca110b113dab733cba829ca1c0ad3305158b46721a537ffc6bb0a898efec8a9e91f0881c45";

    public String extractUserEmail(String token) {return extractClaims(token, Claims::getSubject);}

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){return generateToken(new HashMap<>(),userDetails);}

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60*24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUserEmail(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte [] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
