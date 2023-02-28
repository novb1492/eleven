package com.www.eleven.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.port}")
    public int port;
    @Value("${spring.redis.host}")
    public String host;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        //aws redis사용시
//        if(mode.equals("1")){
//            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
//            clusterConfiguration.clusterNode(host, port);
//            LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
//                    .clientOptions(ClientOptions.builder()
//                            .socketOptions(SocketOptions.builder()
//                                    .build())
//                            .build()).build();
//            return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
//        }
        return new LettuceConnectionFactory(host,port);

    }
    @Bean
    public RedisTemplate<String,Object> redisTemplate() {
        RedisTemplate<String,Object>redisTemplate=new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
