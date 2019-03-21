package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Cdr;
import com.csp.app.service.CdrService;
import com.csp.app.util.MyExcelExportUtil;
import com.csp.app.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chengsp on 2019年1月14日12:00:42
 */
@RequestMapping("/cdr")
@Controller
public class CdrController {
    JedisPool jedisPool;
    private final static Logger logger = LoggerFactory.getLogger(CdrController.class);
    @Autowired
    private CdrService cdrService;
    @Autowired
    private RedisUtil redisUtil;
    @PostMapping("/importCdr")
    @ResponseBody
    public String importCdr(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Object[][] objects = MyExcelExportUtil.read(is, 0, file.getOriginalFilename());
        List<Cdr> cdrs = new ArrayList<>();
        for (int i = 1; i < objects.length; i++) {
            try {
                Object[] object = objects[i];
                Cdr cdr = new Cdr();
                cdr.setCallee((String) object[0]);
                cdr.setCaller((String) object[1]);
                cdr.setStartTime((String) object[2]);
                cdr.setEndTime((String) object[3]);
                cdr.setCallDuration((String) object[4]);
                cdr.setBillDuration(object[5].toString());
                cdr.setStopReason(object[6].toString());
                cdr.setHungup((String) object[7]);
                cdr.setName((String) object[8]);
                cdr.setAppId((String) object[9]);
                cdr.setAreaName((String) object[10]);
                cdrs.add(cdr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            cdrService.insertBatch(cdrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseBuilder.buildSuccess("成功", objects).toString();
    }

    @RequestMapping("/queryByEntity")
    @ResponseBody
    public String queryByEntity(Cdr cdr) {
        List<Cdr> cdrs = cdrService.selectList(new EntityWrapper<>(cdr));
        return ResponseBuilder.buildSuccess("成功!", cdrs).toString();
    }

    @RequestMapping("/queryPart")
    @ResponseBody
    public String queryAll() {
        List<Cdr> cdrs = cdrService.selectPart();
        return ResponseBuilder.buildSuccess("成功!", cdrs).toString();
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        logger.info("向redis里缓存哈希表....");
        Map<String,Object> map = new HashMap<>();
        for (int i = 0; i <50000; i++) {
            map.put(i+"",50000-i);
        }
        redisUtil.hmset("num",map,0);
        return null;
    }

    @RequestMapping("/get")
    @ResponseBody
    public String test(String key) {
        String num = redisUtil.hget("num", String.valueOf(key), 0);
        logger.info("读取哈希表值:{}",num);
        return num;
    }

}
