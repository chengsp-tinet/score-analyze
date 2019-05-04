package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.csp.app.common.BaseController;
import com.csp.app.common.CacheService;
import com.csp.app.common.Const;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.common.es.EsQueryCondition;
import com.csp.app.common.es.EsQueryResult;
import com.csp.app.common.es.JestService;
import com.csp.app.entity.TestEntity;
import com.csp.app.service.RedisService;
import com.csp.app.service.TestService;
import com.csp.app.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chengsp on 2019年1月14日12:00:42
 */
@RequestMapping("/inside/cdr")
@Controller
public class TestController extends BaseController {
    @Autowired
    private JestService jestService;
    private final static Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private TestService cdrService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private List<CacheService> cacheServices;

    @PostMapping("/importCdr")
    @ResponseBody
    public String importCdr(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream is = null;
        List<TestEntity> testEntities = null;
        try {
            is = file.getInputStream();
            testEntities = ExcelUtil.read(is, 0, file.getOriginalFilename(), null, TestEntity.class);
            cdrService.insertBatch(testEntities);
            logger.info("导入成功!");
        } catch (Exception e) {
            logger.error("导入发生异常:{}", e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return ResponseBuilder.buildSuccess("成功", testEntities).toString();
    }

    @RequestMapping("/queryByEntity")
    @ResponseBody
    public String queryByEntity(TestEntity testEntity) {
        List<TestEntity> testEntities = cdrService.selectList(new EntityWrapper<>(testEntity));
        return ResponseBuilder.buildSuccess("成功!", testEntities).toString();
    }

    @RequestMapping("/queryPart")
    @ResponseBody
    public String queryAll() {
        List<TestEntity> testEntities = cdrService.selectPart();
        return ResponseBuilder.buildSuccess("成功!", testEntities).toString();
    }

    @RequestMapping("/export")
    @ResponseBody
    public void export(HttpServletResponse response) {

        int size = 50;
        EntityWrapper<TestEntity> wrapper = new EntityWrapper<>();
        TestEntity entity = new TestEntity();
        entity.setStatus(21);
        wrapper.setEntity(entity);
        //List<TestEntity> cdrs = cdrService.selectList(wrapper);
        try {
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("test.csv", "utf-8"));
            ServletOutputStream out = response.getOutputStream();
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(out));
            ExcelUtil.ExportByPageHelper<TestEntity> exportByPageHelper = new ExcelUtil.ExportByPageHelper<>(br);
            int total = cdrService.selectCount(wrapper);
            wrapper.orderBy("start_time");
            int pageNum = 1;
            while ((pageNum - 1) * size < total) {
                Page<TestEntity> page = new Page<>(pageNum, size);
//                page.setAscs(Arrays.asList("start_time"));
                cdrService.selectPage(page, wrapper);
                pageNum++;
                exportByPageHelper.exportAsCscByPage(page.getRecords(), br, null, null);
            }
            exportByPageHelper.close();
//            ExcelUtil.exportAsCsv(cdrs, null, null, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/test")
    @ResponseBody
    public List<HashMap> test() {

        EsQueryCondition condition = EsQueryCondition.newEsQueryCondition();
        condition.setClasz(HashMap.class);
        EsQueryResult<Object> tempResult = jestService.search(condition);
        condition.setLimit(tempResult.getTotal());
        EsQueryResult<HashMap> result = jestService.search(condition);
        List<HashMap> searchResult = result.getResult();
        return searchResult;
    }

    @RequestMapping("/test2")
    @ResponseBody
    public String test2() {
        logger.info("向redis里缓存哈希表....");
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < 50000; i++) {
            map.put(i + "", 50000 - i);
        }
        redisService.hmset("num", map, 0);
        return null;
    }

    @RequestMapping("/get")
    @ResponseBody
    public int test(String key) {
       /* long n1 = System.currentTimeMillis();
        long m = 0;
        for (int i = 0; i < 20000000; i++) {
            m += i;
        }
        long n2 = System.currentTimeMillis();
        logger.info("耗时:" + String.valueOf(n2 - n1));*/
        String num = redisService.hget("num", String.valueOf(key), 0);
        logger.info("读取哈希表值:{}", num);
        return Integer.parseInt(num);
    }

    @RequestMapping("/reLoadCache")
    @ResponseBody
    public void reLoadCache() {
        redisService.flushDB(Const.DEFAULT_INDEX);
        for (CacheService cacheService : cacheServices) {
            cacheService.loadCache();
            cacheService.flushLocalCache();
        }
    }
}
