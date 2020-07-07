package Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RedisService<T> {
    private static final ObjectMapper om = new ObjectMapper();

    private static String getUrl() {
//        return Optional.ofNullable(System.getenv("REDIS_URL")).orElse("http://192.168.1.218:6379");
        return Optional.ofNullable(System.getenv("REDIS_URL")).orElse("redis://h:pd5017efeaaca244d08df9b543be12e2d19931ad249f27ccd667ac19921a909f2@ec2-52-48-208-62.eu-west-1.compute.amazonaws.com:32449");
    }

    private static Jedis getConnection() throws URISyntaxException {
        URI redisURI = new URI(getUrl());
        return new Jedis(redisURI);
    }

    public static JedisPool getPool() throws URISyntaxException {
        URI redisURI = new URI(getUrl());
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        return new JedisPool(poolConfig, redisURI);
    }

    public void put(String key, T value) {
        if (value == null) {
            throw new IllegalStateException();
        }
        try (Jedis jedis = getConnection()) {
            jedis.set(key, om.writeValueAsString(value));
        } catch (URISyntaxException | JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public void putString(String key, String value) {
        if (value == null) {
            throw new IllegalStateException();
        }
        try (Jedis jedis = getConnection()) {
            jedis.set(key, value);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public void remove(String key) {
        try (Jedis jedis = getConnection()) {
            jedis.del(key);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<T> get(String key) {
        try (Jedis jedis = getConnection()) {
            String value = jedis.get(key);
            return Optional.ofNullable(om.readValue(value, new TypeReference<T>() {
            }));
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<String> getString(String key) {
        try (Jedis jedis = getConnection()) {
            String value = jedis.get(key);
            return Optional.ofNullable(value);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public Set<String> keys() {
        try (Jedis jedis = getConnection()) {
            return jedis.keys("*");
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }


   /* public List<String> sortList(List<String> list) {
        for(String el : list){
            String[] arr = el.split(" ");
            list.add(this.get(key)+" "+key);
        }
        sortList(list);
    }*/
}
