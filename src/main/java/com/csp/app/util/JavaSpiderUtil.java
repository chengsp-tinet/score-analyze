package com.csp.app.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.util.StringUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class JavaSpiderUtil {
    private static Logger logger = LoggerFactory.getLogger(JavaSpiderUtil.class);
    /**
     * 根据给定的URL，获取相应的HTML内容，用于正则爬取数据
     */
    public static String getHtmlByUrl(String requestUrl) throws Exception {
        URL url = new URL(requestUrl);
        //通过Jsoup获取html内容，设置超时时间30秒
        String html = Jsoup.parse(url, TIME_OUT * 1000).toString();
        return html;
    }

    /**
     * 根据URL，发送GET请求，获取JSON数据(ResponseBody)
     * 适用于前后端分离的情况，返回的是ResponseBody的JSon数据
     */
    public static String getResponseBodyByUrlAndMethodGet(String requestUrl) throws Exception {
        String response = "";
        StringBuffer buffer = new StringBuffer();
        try {
            //实例化URL对象，通过String requestURL
            URL url = new URL(requestUrl);
            //调用URL的openConnection()，获得HttpURLConnection实例
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            //状态码是200，则连接成功
            if (200 == urlCon.getResponseCode()) {
                //获得该HttpURLConnection的输入流
                InputStream is = urlCon.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);

                String str = null;
                while ((str = br.readLine()) != null) {
                    //读取该url的ResponseBody(通过输入流转换的BufferedReader)
                    buffer.append(str);
                }
                //根据打开顺序，倒序关流
                br.close();
                isr.close();
                is.close();
                //获得ResponseBody的Json数据
                response = buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 根据给定的url解析图片链接
     * @param url
     * @param baseUrl
     * @return
     */
    public static Set<String> getImgUrls(String url, String baseUrl) {
        try {
            return getImgUrls(Jsoup.parse(new URL(url), TIME_OUT * 1000), baseUrl);
        } catch (IOException e) {
            logger.error("getImgUrls error:{}",e);
        }
        return new LinkedHashSet<>();
    }

    /**
     * 根据给定的dom对象解析图片链接
     * @param document
     * @param baseUrl
     * @return
     */
    public static Set<String> getImgUrls(Document document, String baseUrl) {
        Set<String> urls = new LinkedHashSet<>();
        Elements elements = document.getElementsByTag("img");
        for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            String src = element.attr("src");
            if (StringUtil.isNotEmpty(src)) {
                if (src.startsWith("http") || src.startsWith("https")) {
                    urls.add(src);
                } else {
                    if (StringUtil.isNotEmpty(src)) {
                        String s;
                        if (baseUrl.endsWith("/") && src.endsWith("/")) {
                            s = baseUrl + src.substring(1);
                        } else if ((!baseUrl.endsWith("/")) && (!src.endsWith("/"))) {
                            s = baseUrl + "/" + src;
                        } else {
                            s = baseUrl + src;
                        }
                        urls.add(s);
                    }
                }
            }
        }
        return urls;
    }

    /**
     * 根据给定的dom对象解析所有指定标签的属性
     * @param document
     * @param tagName
     * @param attr
     * @return
     */
    public static Set<String> getElementAttrs(Document document, String tagName, String attr) {
        Set<String> urls = new LinkedHashSet<>();
        Elements elements = document.getElementsByTag(tagName);
        for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            String prop = element.attr(attr);
            if (StringUtil.isNotEmpty(prop)) {
                urls.add(prop);
            }
        }
        return urls;
    }

    /**
     * 根据给定的dom对象解析链接
     * @param document
     * @param baseUrl
     * @return
     */
    public static List<String> getUrls(Document document, String baseUrl) {
        Elements elements = document.getElementsByTag("a");
        for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            String href = element.attr("href");
            if (StringUtil.isNotEmpty(href)) {
                if (href.equalsIgnoreCase("javascript:;")) {
                    continue;
                }
                if (href.startsWith("http") || href.startsWith("https")) {
                    if (!(urlSet.contains(href) || urlList.contains(href))) {
                        urlList.add(href);
                    }
                    urlSet.add(href);
                } else {
                    if (StringUtil.isNotEmpty(href)) {
                        String s;
                        if (baseUrl.endsWith("/") && href.endsWith("/")) {
                            s = baseUrl + href.substring(1);
                        } else if ((!baseUrl.endsWith("/")) && (!href.endsWith("/"))) {
                            s = baseUrl + "/" + href;
                        } else {
                            s = baseUrl + href;
                        }
                        if (!(urlSet.contains(s) || urlList.contains(s))) {
                            urlList.add(s);
                        }
                        urlSet.add(s);
                    }
                }
            }
        }
        return urlList;
    }

    /**
     * 根据给定得url解析链接
     * @param url
     * @param baseUrl
     * @return
     */
    public static List<String> getUrls(String url, String baseUrl) {
        try {
           logger.info("访问:" + url);
            Document document = Jsoup.parse(new URL(url), TIME_OUT * 1000);
            return getUrls(document, baseUrl);
        } catch (Exception e) {
            logger.error("getUrls error:{}",e);
        }
        return urlList;
    }

    /**
     * 下载文件
     * @param url
     * @param savePath
     * @param fileName
     * @return
     */
    public static boolean downloadFile(String url, String savePath, String fileName) {
        try {
            logger.info("下载:" + url);
            return downloadFile(new URL(url), savePath, fileName);
        } catch (MalformedURLException e) {
            logger.error("downloadFile error:{}",e);
        }
        return false;
    }

    /**
     * 批量下载文件
     * @param urlCollection
     * @param savePath
     */
    public static void downloadFiles(Collection<String> urlCollection, String savePath) {
        for (Iterator<String> iterator = urlCollection.iterator(); iterator.hasNext(); ) {
            String url = iterator.next();
            if (!visitedFileUrl.contains(url)) {
                downloadFile(url, savePath, null);
            }
            visitedFileUrl.add(url);
        }
    }

    /**
     * 下载文件
     * @param url
     * @param savePath
     * @param fileName
     * @return
     */
    public static boolean downloadFile(URL url, String savePath, String fileName) {
        try {
            return downloadFile(url.openConnection(), savePath, fileName);
        } catch (IOException e) {
            logger.error("downloadFile error:{}",e);
        }
        return false;
    }

    /**
     * 根据url判断图片类型
     * @param url
     * @return
     */
    private static String getImgType(URL url) {
        String urlStr = url.getPath();
        int n = urlStr.lastIndexOf(".");
        if(n == -1){
            return ".jpg";
        }
        return urlStr.substring(n);
    }

    /**
     * 根据urlConnection对象下载文件
     * @param urlConnection
     * @param savePath
     * @param fileName
     * @return
     */
    public static boolean downloadFile(URLConnection urlConnection, String savePath, String fileName) {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            urlConnection.setConnectTimeout(TIME_OUT * 1000);
            // 输入流
            is = urlConnection.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdir();
            }
            if (file.isDirectory()) {
                if (StringUtil.isNotEmpty(fileName)) {
                    fos = new FileOutputStream(savePath + "/" + fileName);
                } else {
                    String fileTime = DateUtil.format(new Date(), "yyyyMMddhhmmssSSS") + getImgType(urlConnection.getURL());
                    fos = new FileOutputStream(savePath + "/" + fileTime);
                }
            } else {
                fos = new FileOutputStream(savePath);
            }
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                fos.write(bs, 0, len);
            }
            logger.info(++n + "条数据");
            // 完毕，关闭所有链接
            return true;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 所有的url链接
     */
    private static LinkedList<String> urlList = new LinkedList<>();
    /**
     * 用来去重的url集合
     */
    private static Set<String> urlSet = new HashSet<>();
    /**
     * 已访问过的文件链接
     */
    private static Set<String> visitedFileUrl = new LinkedHashSet<>();
    private static int n = 0;
    private static final int TIME_OUT = 5;

    /**
     * 广度优先爬取
     * @param url
     * @param baseUrl
     * @param savePath
     */
    public static void download(String url, String baseUrl, String savePath) {
        Set<String> imgUrls = getImgUrls(url, baseUrl);
        downloadFiles(imgUrls, savePath);
        getUrls(url, baseUrl);
        while (true) {
            try {
                String currentUrl = urlList.pop();
                getUrls(currentUrl, baseUrl);
                Set<String> imgUrlList = getImgUrls(currentUrl, baseUrl);
                downloadFiles(imgUrlList, savePath);
            } catch (Exception e) {
                logger.error("download error:{}",e);
            }
        }
    }

}