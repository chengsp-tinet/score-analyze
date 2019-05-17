package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.entity.Admin;

public interface AdminService extends IService<Admin> {
    Admin login(Admin admin);
}
