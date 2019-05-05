package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.common.CacheService;
import com.csp.app.entity.Clasz;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClaszService extends IService<Clasz>, CacheService<Clasz> {
    /**
     * 查询所有
     *
     * @return
     */
    List<Clasz> searchAll();

    /**
     * 添加单条记录
     *
     * @param clasz
     * @return
     */
    boolean add(Clasz clasz);

    /**
     * 批量添加班级
     *
     * @param claszs
     * @return
     */
    boolean batchAdd(List<Clasz> claszs);
}
