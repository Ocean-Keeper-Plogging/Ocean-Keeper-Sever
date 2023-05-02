package com.server.oceankeeper.Global.Jwt;

public class JwtConfig {

    public static final String SECRET = "나중에환경변수설정"; //to-do 환경변수 설정하기 

    public static final int EXPIRATION_TIME = 1000*60*60*24*7;// 일주일


    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String HEADER = "Authorization";

}
