package com.csp.app.common;

import com.csp.app.util.DateUtil;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseController {
    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FMT_DATE_YYYY_MM_DD_HH_mm_ss);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
