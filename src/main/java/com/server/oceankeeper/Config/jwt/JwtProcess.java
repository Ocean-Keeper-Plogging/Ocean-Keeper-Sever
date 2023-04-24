package com.server.oceankeeper.Config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.server.oceankeeper.Config.Auth.LoginUser;
import com.server.oceankeeper.User.User;
import com.server.oceankeeper.User.UserEnum.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JwtProcess {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static Long toUserId(String token){
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtConfig.SECRET)).build().verify(token);
        Long id = decodedJWT.getClaim("id").asLong();

        return id;
    }
    public static String create(LoginUser loginUser){
        String jwtToken = JWT.create()
                .withSubject("Ocean Keeper")
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtConfig.EXPIRATION_TIME))
                .withClaim("id", loginUser.getUser().getId())
                .withClaim("role", loginUser.getUser().getRole()+"")
                .sign(Algorithm.HMAC512(JwtConfig.SECRET));

        return JwtConfig.TOKEN_PREFIX + jwtToken;

    }

    public static LoginUser verify(String token){
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtConfig.SECRET)).build().verify(token);
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString();
        User user = User.builder().id(id).role(UserRole.valueOf(role)).build();

        LoginUser loginUser = new LoginUser(user);

        return loginUser;
    }



}
