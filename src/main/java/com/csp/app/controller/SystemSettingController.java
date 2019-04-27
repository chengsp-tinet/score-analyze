package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.csp.app.common.BaseController;
import com.csp.app.common.CacheKey;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.SystemSetting;
import com.csp.app.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author chengsp on 2019年1月14日12:00:42
 */
@RequestMapping("/systemSetting")
@Controller
public class SystemSettingController extends BaseController {
    @Autowired
    private SystemSettingService systemSettingService;

    @RequestMapping("/add")
    @ResponseBody
    public boolean add(SystemSetting systemSetting) {
        return systemSettingService.insert(systemSetting);
    }

    @RequestMapping("/queryByEntity")
    @ResponseBody
    public SystemSetting queryByEntity(String name) {
        return systemSettingService.getEntityFromLocalCacheByKey(String.format(CacheKey.SYSTEM_SETTING_NAME_SYSTEM_SETTING, name));
    }

    @RequestMapping("/queryAll")
    @ResponseBody
    public String queryAll() {
        List<SystemSetting> systemSettings = systemSettingService.selectList(new EntityWrapper<>(null));
        return ResponseBuilder.buildSuccess("成功", systemSettings).toString();
    }
}
