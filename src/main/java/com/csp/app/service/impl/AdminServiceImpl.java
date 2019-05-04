package com.csp.app.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.csp.app.entity.Admin;
import com.csp.app.mapper.AdminMapper;
import com.csp.app.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Override
    public Admin login(Admin admin) {
        EntityWrapper<Admin> wrapper = new EntityWrapper<>(admin);
        return selectOne(wrapper);
    }
}
