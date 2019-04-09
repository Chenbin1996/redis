package com.ruxuanwo.common.redis.config;


import com.ruxuanwo.common.redis.client.RedisClient;
import com.ruxuanwo.common.redis.client.impl.JedisClientCluster;
import com.ruxuanwo.common.redis.client.impl.JedisClientSingle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * redis客户端，单机还是集群
 *
 * @author chenbin
 */
@Configuration
public class RedisClientConfiguration {
    @Bean
    public RedisClient getJedisClient(JedisPool jedisPool, JedisCluster jedisCluster) {
        if (jedisCluster != null) {
            return new JedisClientCluster(jedisCluster);
        }
        if (jedisPool != null) {
            return new JedisClientSingle(jedisPool);
        }
        return null;
    }
}
