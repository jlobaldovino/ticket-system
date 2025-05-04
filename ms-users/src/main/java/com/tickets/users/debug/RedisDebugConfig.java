package com.tickets.users.debug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RedisDebugConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisDebugConfig.class);

    @Bean
    public CommandLineRunner debugRedisConfig(Environment env,
                                              @Value("${spring.redis.host:not-set}") String redisHost,
                                              @Value("${spring.redis.port:0}") int redisPort) {
        return args -> {
            logger.info("==== DEPURACIÓN DE CONFIGURACIÓN REDIS ====");
            logger.info("Perfiles activos: {}", String.join(", ", env.getActiveProfiles()));
            logger.info("Redis host configurado: {}", redisHost);
            logger.info("Redis port configurado: {}", redisPort);
            logger.info("Valor directo desde Environment: {}", env.getProperty("spring.redis.host"));
            logger.info("===========================================");
        };
    }
}