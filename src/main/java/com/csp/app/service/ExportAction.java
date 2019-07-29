package com.csp.app.service;

import javax.servlet.http.HttpServletResponse;

public interface ExportAction<T> {
    void export(T t, HttpServletResponse response);
}
