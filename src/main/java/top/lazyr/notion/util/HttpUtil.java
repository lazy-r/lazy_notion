package top.lazyr.notion.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lazyr
 * @created 2021/12/16
 */
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String post(String url, Map<String, String> headers, String body) {
        // 1、打开浏览器
        CloseableHttpClient client = HttpClients.createDefault();
        // 2、输入网址
        HttpPost httpPost = new HttpPost(url);

        // 3、设置headers
        for (String key : headers.keySet()) {
            httpPost.setHeader(key, headers.get(key));
        }

        // 4、设置body
        StringEntity stringEntity = new StringEntity(body, "UTF-8");
        httpPost.setEntity(stringEntity);

        // 5、按下Enter
        String content = enter(client, httpPost);
        return content;
    }

    public static String get(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36 Edg/89.0.774.76");
        return  get(url, headers);
    }

    public static String get(String url, Map<String, String> headers) {
        // 1、打开浏览器
        CloseableHttpClient client = HttpClients.createDefault();
        // 2、输入网址
        HttpGet httpGet = new HttpGet(url);

        // 3、设置headers
        if (headers != null) {
            for (String key : headers.keySet()) {
                httpGet.setHeader(key, headers.get(key));
            }
        }

        // 4、设置参数
//        HttpParams params = new BasicHttpParams();
//        httpGet.getParams().setParameter("http.protocol.allow-circular-redirects", true);

        // 5、按下Enter
        String content = enter(client, httpGet);
        return content;

    }


    private static String enter(CloseableHttpClient client, HttpRequestBase request) {
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 6、判断响应状态
        if(response.getStatusLine().getStatusCode() != 200){
            logger.error("get " + request.getURI() + " status code = " + response.getStatusLine().getStatusCode());
            return response.getStatusLine() + "";
        }

        // 7、解析响应
        HttpEntity entity = response.getEntity();
        String content = "";
        try {
            content = EntityUtils.toString(entity);
            close(response, client);

        } catch (IOException e) {
            content = "parse content failed: " + e.getMessage();
            logger.error("parse content failed: " + e.getMessage());
        }

        return content;
    }

    private static void close(CloseableHttpResponse response, CloseableHttpClient client) {
        try {
            if (response != null) {
                response.close();
            }

            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
