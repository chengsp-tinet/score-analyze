package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.common.CachService;
import com.csp.app.entity.SystemSetting;

public interface SystemSettingService extends IService<SystemSetting> , CachService<SystemSetting> {
}
