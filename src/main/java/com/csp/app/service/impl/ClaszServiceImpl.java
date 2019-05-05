package com.csp.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.csp.app.common.CacheKey;
import com.csp.app.common.Const;
import com.csp.app.entity.Clasz;
import com.csp.app.mapper.ClaszMapper;
import com.csp.app.service.ClaszService;
import com.csp.app.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengsp
 */
@Service
public class ClaszServiceImpl extends ServiceImpl<ClaszMapper, Clasz> implements ClaszService {
    private final static Logger logger = LoggerFactory.getLogger(ClaszServiceImpl.class);
    private static final Clasz NULL_ENTITY = new Clasz();
    private static Map<String, Clasz> localCache = new ConcurrentHashMap<>(32);
    @Autowired
    private ClaszMapper claszMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public Clasz getEntityFromLocalCacheByKey(String key) {
        Clasz localEntity = localCache.get(key);
        if (localEntity == null) {
            Clasz redisEntity = redisService.getObject(key, Const.DEFAULT_INDEX, Clasz.class);
            if (redisEntity == null) {
                localCache.put(key, NULL_ENTITY);
                return null;
            } else {
                localCache.put(key, redisEntity);
                return redisEntity;
            }
        } else {
            return localEntity == NULL_ENTITY ? null : localEntity;
        }
    }

    @Override
    public void loadCache() {
        List<Clasz> classs = claszMapper.selectList(null);
        for (Clasz clasz : classs) {
            redisService.setObject(String.format(CacheKey.CLASS_ID_CLASS, clasz.getClassId())
                    , clasz, Const.DEFAULT_INDEX);
        }
        redisService.setObject(CacheKey.CLASS_ALL, classs, Const.DEFAULT_INDEX);
        logger.info("缓存Clasz{}条", classs.size());
    }

    @Override
    public void flushLocalCache() {
        localCache.clear();
        logger.info("清空本地缓存{}条", localCache.size());
    }

    @Override
    public List<Clasz> searchAll() {
        String claszsStr = redisService.getString(CacheKey.CLASS_ALL, Const.DEFAULT_INDEX);
        List<Clasz> claszs;
        if (StringUtil.isNotEmpty(claszsStr)) {
            claszs = JSON.parseArray(claszsStr, Clasz.class);
        } else {
            claszs = selectList(null);
            redisService.setObject(CacheKey.CLASS_ALL, claszs, Const.DEFAULT_INDEX);
        }
        return claszs;
    }

    @Override
    public boolean add(Clasz clasz) {
        completeEntity(clasz);
        return claszMapper.insert(clasz) == 1;
    }

    @Override
    public boolean batchAdd(List<Clasz> claszs) {
        for (Clasz clasz : claszs) {
            completeEntity(clasz);
        }
        return insertBatch(claszs);
    }

    private void completeEntity(Clasz clasz) {
        Integer toSchoolYear = clasz.getToSchoolYear();
        Integer classNum = clasz.getClassNum();
        Integer type = clasz.getType();
        if (type != Const.CLASS_TYPE_PRIMARY && type != Const.CLASS_TYPE_MIDDLE) {
            throw new RuntimeException("不合法的班级类型");
        }
        Integer classId = toSchoolYear * 1000 + classNum * 10 + type;
        clasz.setClassId(classId);
    }
}
