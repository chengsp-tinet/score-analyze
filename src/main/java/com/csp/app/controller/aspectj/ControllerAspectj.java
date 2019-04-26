package com.csp.app.controller.aspectj;

import com.alibaba.fastjson.JSON;
import com.csp.app.entity.OperateLog;
import com.csp.app.service.OperateLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chengsp on 2019/1/8.
 */
@Aspect
@Component
public class ControllerAspectj {

    private static Logger logger = LoggerFactory.getLogger(ControllerAspectj.class);
    @Autowired
    private OperateLogService operateLogService;

    @Pointcut("execution(public * com.csp.app.controller.SystemSettingController.*(..))")
    private void aspectJMethod() {
    }

    @Around("aspectJMethod()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String requestURI = request.getRequestURI();
            Object[] args = pjp.getArgs();
            Signature signature = pjp.getSignature();
            String method = signature.getName();
            Class<?> classTarget = pjp.getTarget().getClass();
            Class<?>[] par = ((MethodSignature) pjp.getSignature()).getParameterTypes();
            Method objMethod = classTarget.getMethod(method, par);
            OperateLog log = new OperateLog();
            log.setCreateTime(new Date());
            log.setParam(JSON.toJSONString(getParam(args, ((CodeSignature) signature).getParameterNames())));
            log.setInterfaceUrl(requestURI);
            log.setMethod(classTarget.getName() + "." + objMethod.getName());
            log.setUsername("chengsp");
            operateLogService.saveLog(log);
        } catch (NoSuchMethodException e) {
            logger.error("{}",e);
        } catch (SecurityException e) {
            logger.error("{}",e);
        }
        return result;
    }

    @AfterThrowing(value = "aspectJMethod()", throwing = "e")
    public void doThrowing(JoinPoint joinPoint, Exception e) {
        logger.info("发生异常后置处理");
    }


    /**
     * 获取参数Map集合
     * @return
     */
    private Map<String, Object> getParam(Object[] paramValues, String[] paramNames) {
        Map<String, Object> param = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], paramValues[i]);
        }

        return param;
    }

}
