package com.csp.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 解决session共享问题配置
 * @author chengsp on 2018年12月27日21:10:22
 */
@Configuration
@EnableRedisHttpSession
public class SessionConfig {
    @Bean
    public static ConfigureRedisAction configureRedisAction() {

        return ConfigureRedisAction.NO_OP;

    }
}
