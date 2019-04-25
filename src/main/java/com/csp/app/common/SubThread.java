package com.csp.app.common;

import com.csp.app.service.RedisService;
import com.csp.app.util.ContextUtil;

/**
 * @author chengsp on 2019年4月5日15:01:18
 */
public class SubThread extends Thread {
    public SubThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        ContextUtil.getBean(RedisService.class).subscribe(new Subscriber(), Const.DEFAULT_CHANNEL);
    }
}
