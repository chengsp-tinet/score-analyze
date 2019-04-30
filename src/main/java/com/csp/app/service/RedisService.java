package com.csp.app.service;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * redis操作类
 *
 * @author admin
 */
@Service
public class RedisService {
    private Logger logger = LoggerFactory.getLogger(RedisService.class);
    @Autowired
    private JedisPool jedisPool;
    private static final String ENCODING = "UTF-8";
    @Autowired
    private RedisTemplate redisTemplate;

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
        }
        return incr;
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
                connection.set(key.getBytes(ENCODING), JSON.toJSONString(value).getBytes(ENCODING));
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
            return bytes == null ? null : new String(bytes, ENCODING);
        } catch (Exception e) {
            logger.error("redis get error:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
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
            String jsonStr;
            if (bytes != null) {
                jsonStr = new String(bytes, ENCODING);
                return JSON.parseObject(jsonStr, tClass);
            }
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
        Long incr = 0L;
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
        }
        return incr;
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
        Long ttl = (long) -1;
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
        }
        return ttl;
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
                tempMap.put(entry.getKey().getBytes(ENCODING), JSON.toJSONString(entry.getValue()).getBytes(ENCODING));
            }
            connection.hMSet(key.getBytes(ENCODING), tempMap);
        } catch (UnsupportedEncodingException e) {
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
            connection.hSet(key.getBytes(ENCODING), field.getBytes(ENCODING), JSON.toJSONString(value).getBytes(ENCODING));
        } catch (UnsupportedEncodingException e) {
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

    /**
     * 订阅多个频道
     *
     * @param jedisPubSub
     * @param channelName
     * @return
     */
    public boolean subscribe(JedisPubSub jedisPubSub, String... channelName) {
        Jedis jedis = this.jedisPool.getResource();
        try {
            jedis.subscribe(jedisPubSub, channelName);
            return true;
        } catch (Exception e) {
            logger.error("订阅频道异常:{}", e);
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 发布消息
     *
     * @param channelName
     * @param msg
     * @return
     */
    public Long publish(String channelName, String msg) {
        Long result = -1L;
        Jedis jedis = this.jedisPool.getResource();
        try {
            result = jedis.publish(channelName, msg);
        } catch (Exception e) {
            logger.error("发布消息异常:{}", e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 发布消息
     *
     * @param channel
     * @param message
     */
    public void convertAndSend(String channel, Object message) {
        if (message instanceof String) {
            redisTemplate.convertAndSend(channel, ((String) message));
        } else {
            redisTemplate.convertAndSend(channel, JSON.toJSON(message));
        }
    }

    /**
     * 清空某库
     * @param index
     */
    public void flushDB(int index){
        RedisConnection connection = getConnection();
        connection.select(index);
        connection.flushDb();
    }
}
