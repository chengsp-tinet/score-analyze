package com.csp.app.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpUtil {
    public HttpUtil() {
    }

    public static Map get(String url) throws IOException {
        return get(url, (Map)null);
    }

    public static Map get(String url, Map<String, String> headers) throws IOException {
        return fetch("GET", url, (String)null, headers);
    }

    public static Map post(String url, String body, Map<String, String> headers) throws IOException {
        return fetch("POST", url, body, headers);
    }

    public static Map post(String url, String body) throws IOException {
        return post(url, body, (Map)null);
    }

    public static Map postForm(String url, Map<String, String> params) throws IOException {
        return postForm(url, params, (Map)null);
    }

    public static Map postForm(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        if(headers == null) {
            headers = new HashMap();
        }

        ((Map)headers).put("Content-Type", "application/x-www-form-urlencoded");
        String body = "";
        if(params != null) {
            boolean first = true;

            String value;
            for(Iterator var5 = params.keySet().iterator(); var5.hasNext(); body = body + URLEncoder.encode(value, HTTP.UTF_8)) {
                String param = (String)var5.next();
                if(first) {
                    first = false;
                } else {
                    body = body + "&";
                }

                value = (String)params.get(param);
                body = body + URLEncoder.encode(param, HTTP.UTF_8) + "=";
            }
        }

        return post(url, body, (Map)headers);
    }

    public static Map put(String url, String body, Map<String, String> headers) throws IOException {
        return fetch("PUT", url, body, headers);
    }

    public static Map put(String url, String body) throws IOException {
        return put(url, body, (Map)null);
    }

    public static Map delete(String url, Map<String, String> headers) throws IOException {
        return fetch("DELETE", url, (String)null, headers);
    }

    public static Map delete(String url) throws IOException {
        return delete(url, (Map)null);
    }

    public static String appendQueryParams(String url, Map<String, String> params) throws IOException {
        String fullUrl = url;
        if(params != null) {
            boolean first = url.indexOf(63) == -1;

            String value;
            for(Iterator var4 = params.keySet().iterator(); var4.hasNext(); fullUrl = fullUrl + URLEncoder.encode(value, HTTP.UTF_8)) {
                String param = (String)var4.next();
                if(first) {
                    fullUrl = fullUrl + '?';
                    first = false;
                } else {
                    fullUrl = fullUrl + '&';
                }

                value = (String)params.get(param);
                fullUrl = fullUrl + URLEncoder.encode(param, HTTP.UTF_8) + '=';
            }
        }

        return fullUrl;
    }

    public static Map<String, String> getQueryParams(String url) throws IOException {
        Map<String, String> params = new HashMap();

        String param;
        String value;
        for(int start = url.indexOf(63); start != -1; params.put(URLDecoder.decode(param, HTTP.UTF_8), URLDecoder.decode(value, HTTP.UTF_8))) {
            int equals = url.indexOf(61, start);
            param = "";
            if(equals != -1) {
                param = url.substring(start + 1, equals);
            } else {
                param = url.substring(start + 1);
            }

            value = "";
            if(equals != -1) {
                start = url.indexOf(38, equals);
                if(start != -1) {
                    value = url.substring(equals + 1, start);
                } else {
                    value = url.substring(equals + 1);
                }
            }
        }

        return params;
    }

    public static String removeQueryParams(String url) throws IOException {
        int q = url.indexOf(63);
        return q != -1?url.substring(0, q):url;
    }

    public static Map fetch(String method, String url, String body, Map<String, String> headers) throws IOException {
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)u.openConnection();
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(5000);
        if(method != null) {
            conn.setRequestMethod(method);
        }

        String response;
        if(headers != null) {
            Iterator var6 = headers.keySet().iterator();

            while(var6.hasNext()) {
                response = (String)var6.next();
                conn.addRequestProperty(response, (String)headers.get(response));
            }
        }

        if(body != null) {
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes(HTTP.UTF_8));
            os.flush();
            os.close();
        }

        InputStream is = conn.getInputStream();
        response = streamToString(is);
        is.close();
        Map map = new HashMap<>();
        map.put("response",response);
        map.put("code",conn.getResponseCode());
        if(conn.getResponseCode() == 301) {
            String location = conn.getHeaderField("Location");
            return fetch(method, location, body, headers);
        } else {
            return map;
        }
    }

    /*public static String postForHttps(String requestUrl, String body, Map<String, String> headers) throws IOException {
        HttpPost postMethod = new HttpPost(requestUrl);

        postMethod.setHeader("Accept", "application/json;charset=UTF-8");
        postMethod.setHeader("Content-Type", "application/json;charset=UTF-8");

        if(headers != null) {
            Iterator var6 = headers.keySet().iterator();

            while(var6.hasNext()) {
                String key = (String)var6.next();
                postMethod.setHeader(key, (String) headers.get(key));
            }
        }

        CloseableHttpClient httpClient = null;
        String result = "";
        try {
            httpClient = new SSLClient();
            postMethod.setEntity(new StringEntity(body, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(postMethod);
            HttpEntity entity = response.getEntity();
            if(entity!=null) {
                result = EntityUtils.toString(entity, HTTP.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }*/

    public static String postForUnicom(String requestUrl, String body) throws IOException {
        if(StringUtils.isBlank(requestUrl)){
            throw new IllegalArgumentException("调用HttpClientUtil.setPostRequest方法，targetUrl不能为空!");
        }

        String responseResult = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        try{

            HttpPost httppost = new HttpPost(requestUrl);
            //设置超时
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(5000).build();//设置请求和传输超时时间
            httppost.setConfig(requestConfig);

            StringEntity entity = new StringEntity(body,"GBK");//解决中文乱码问题
            entity.setContentEncoding("GBK");
            //entity.setContentType("application/json");
            httppost.setEntity(entity);

            /*httppost.setHeader("Accept", "application/json;charset=GBK");
            httppost.setHeader("Content-Type", "application/json;charset=GBK");*/

            response = httpClient.execute(httppost);

            responseResult = EntityUtils.toString(response.getEntity(),"GBK");

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //释放链接
            response.close();
            httpClient.close();

        }

        return responseResult;
    }

    public static String streamToString(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];

        int n;
        while((n = in.read(b)) != -1) {
            out.append(new String(b, 0, n, HTTP.UTF_8));
        }

        return out.toString();
    }
}
