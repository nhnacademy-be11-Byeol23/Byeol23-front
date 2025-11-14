package com.nhnacademy.byeol23front.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Redis Session 설정
 * 
 * 비회원만 세션 사용, 회원은 JWT 토큰으로 인증 처리
 * 30일(2592000초)
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 2592000)
public class RedisSessionConfig {
}

