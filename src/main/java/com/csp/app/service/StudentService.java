package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.common.CacheService;
import com.csp.app.entity.Student;

public interface StudentService extends IService<Student>, CacheService<Student> {
}
