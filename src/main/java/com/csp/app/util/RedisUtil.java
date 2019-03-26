package com.csp.app.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * redis操作类
 *
 * @author admin
 */
@Service
public class RedisUtil {
    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    @Autowired
    private JedisPool jedisPool;
    private static final String ENCODING = "UTF-8";
    @Autowired
    private RedisTemplate redisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public long incrUntil(String key, int db) {
        RedisConnection connection = getConnection();
        Long incr = 0L;
        try {
            if (connection != null) {
                connection.select(db);
                incr = connection.incr(key.getBytes(ENCODING));
                if (incr > 10000000) {
                    set(key, 0, db);
                }
            }
        } catch (Exception e) {
            logger.error("redis incr error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
            return incr;
        }
    }

    public void set(String key, Object value, int db) {
        RedisConnection connection = getConnection();
        try {
            if (connection != null) {
                connection.select(db);
                connection.set(key.getBytes(ENCODING), value.toString().getBytes(ENCODING));
            }
        } catch (Exception e) {
            logger.error("redis set error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * set Object
     *
     * @param key
     * @param value
     * @param db
     * @throws Exception
     */
    public void setObject(String key, Object value, int db) {
        RedisConnection connection = getConnection();
        try {
            if (connection != null) {
                connection.select(db);
                connection.set(key.getBytes(ENCODING), objectMapper.writeValueAsString(value).getBytes(ENCODING));
            }
        } catch (Exception e) {
            logger.error("redis setObject error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * delete key
     *
     * @param key
     * @param db
     */
    public void delete(String key, int db) {
        RedisConnection connection = getConnection();
        try {
            if (connection != null) {
                connection.select(db);
                connection.del(key.getBytes(ENCODING));
            }
        } catch (Exception e) {
            logger.error("redis del error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * 获取String类型的value
     *
     * @param key
     * @param db
     * @return
     */
    public String getString(String key, int db) {
        RedisConnection connection = getConnection();
        byte[] bytes = null;
        try {
            if (connection != null) {
                connection.select(db);
                bytes = connection.get(key.getBytes(ENCODING));
            }
        } catch (Exception e) {
            logger.error("redis get error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
            try {
                return bytes == null ? null : new String(bytes, ENCODING);
            } catch (UnsupportedEncodingException e) {
                logger.error("redis get error:{}", e);
            }
            return null;
        }
    }

    /**
     * 获取Object
     *
     * @param key
     * @param db
     * @param tClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T getObject(String key, int db, Class<T> tClass) {
        RedisConnection connection = getConnection();
        byte[] bytes = null;
        try {
            if (connection != null) {
                connection.select(db);
                bytes = connection.get(key.getBytes(ENCODING));
            }
            return bytes == null ? null : objectMapper.readValue(bytes, tClass);
        } catch (Exception e) {
            logger.error("redis getObject error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    public Long incr(String key, int db) {
        RedisConnection connection = getConnection();
        Long incr = new Long(0);
        try {
            if (connection != null) {
                connection.select(db);
                incr = connection.incr(key.getBytes(ENCODING));
            }
        } catch (Exception e) {
            logger.error("redis incr error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
            return incr;
        }
    }


    public void setEx(String key, Integer second, String value, int db) {
        RedisConnection connection = getConnection();
        try {
            if (connection != null) {
                connection.select(db);
                connection.setEx(key.getBytes(ENCODING), second, value.getBytes(ENCODING));
            }
        } catch (Exception e) {
            logger.error("redis setEx error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public Long ttl(String key, int db) {
        RedisConnection connection = getConnection();
        Long ttl = new Long(-1);
        try {
            if (connection != null) {
                connection.select(db);
                ttl = connection.ttl(key.getBytes(ENCODING));
            }
        } catch (Exception e) {
            logger.error("redis ttl error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
            return ttl;
        }
    }

    private RedisConnection getConnection() {
        return this.redisTemplate.getConnectionFactory().getConnection();
    }

    public boolean hmset(String key, Map<String, Object> map, int db) {

        RedisConnection connection = null;
        try {
            connection = getConnection();
            connection.select(db);
            Map<byte[], byte[]> tempMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                tempMap.put(entry.getKey().getBytes(ENCODING), objectMapper.writeValueAsString(entry.getValue()).getBytes(ENCODING));
            }
            connection.hMSet(key.getBytes(ENCODING), tempMap);
        } catch (UnsupportedEncodingException e) {
            logger.error("hmset error:{}", e);
        } catch (JsonProcessingException e) {
            logger.error("hmset error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return true;
    }

    public boolean hset(String key, String field, Object value, int db) {

        RedisConnection connection = null;
        try {
            connection = getConnection();
            connection.select(db);
            connection.hSet(key.getBytes(ENCODING), field.getBytes(ENCODING), objectMapper.writeValueAsString(value).getBytes(ENCODING));
        } catch (UnsupportedEncodingException e) {
            logger.error("hset error:{}", e);
        } catch (JsonProcessingException e) {
            logger.error("hset error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return true;
    }

    public String hget(String key, String field, int db) {
        RedisConnection connection = null;
        try {
            connection = getConnection();
            connection.select(db);
            byte[] bytes = connection.hGet(key.getBytes(ENCODING), field.getBytes(ENCODING));
            if (bytes != null) {
                return new String(bytes, ENCODING);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("hget error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }
    public void sendMessage(String channel, Object message){
        redisTemplate.convertAndSend(channel, JSON.toJSON(message));
    }
}
