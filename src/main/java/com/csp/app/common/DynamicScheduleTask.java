package com.csp.app.common;

import com.csp.app.util.DateUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;

//@Component
//@Configuration      //1.主要用于标记配置类，兼备Component的效果。
public class DynamicScheduleTask implements SchedulingConfigurer {
    private final static Logger logger = LoggerFactory.getLogger(DynamicScheduleTask.class);
    @Mapper
    public interface CronMapper {
        @Select("select cron from cron limit 1")
        public String getCron();
    }

    @Autowired      //注入mapper
    @SuppressWarnings("all")
    CronMapper cronMapper;

    /**
     * 执行定时任务.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        taskRegistrar.addTriggerTask(
                //1.添加任务内容(Runnable)
                () -> System.out.println("执行动态定时任务: " + LocalDateTime.now().toLocalTime()),
                //2.设置执行周期(Trigger)
                triggerContext -> {
                    Date date = null;
                    String cron = null;
                    try {
                        //2.1 从数据库获取执行周期
                        cron = cronMapper.getCron();
                        //2.2 合法性校验.
                        if (StringUtils.isEmpty(cron)) {
                            // Omitted Code ..
                        }
                        //2.3 返回执行周期(Date)
                        date = new CronTrigger(cron).nextExecutionTime(triggerContext);
                    } catch (Exception e) {
                        logger.error("cron表达式有误: {}", cron);
                        date = DateUtil.addSecond(new Date(), 10);
                    }
                    return date;
                }
        );
    }

}