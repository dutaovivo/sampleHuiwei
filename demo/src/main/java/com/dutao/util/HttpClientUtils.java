package com.dutao.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Created with IDEA
 * @author: Super Zheng
 * @Description: java类作用描述
 * @Date:2018/12/26
 * @Time:12:58
 */
public class HttpClientUtils {

    /**
     * 发送get请求，利用java代码发送请求
     * @param url
     * @return
     * @throws Exception
     */
    public static String doGet(String url) throws Exception{

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        // 发送了一个http请求
        CloseableHttpResponse response = httpclient.execute(httpGet);
        // 如果响应200成功,解析响应结果
        if(response.getStatusLine().getStatusCode()==200){
            // 获取响应的内容
            HttpEntity responseEntity = response.getEntity();

            return EntityUtils.toString(responseEntity);
        }
        return null;
    }

    /**
     * 将字符串转换成map
     * @param responseEntity
     * @return
     */
    public static Map<String,String> getMap(String responseEntity) {

        Map<String, String> map = new HashMap<String, String>();
        // 以&来解析字符串
        String[] result = responseEntity.split("\\&");

        for (String str : result) {
            // 以=来解析字符串
            String[] split = str.split("=");
            // 将字符串存入map中
            if (split.length == 1) {
                map.put(split[0], null);
            } else {
                map.put(split[0], split[1]);
            }

        }
        return map;
    }

    /**
     * 通过json获得map
     * @param responseEntity
     * @return
     */
    public static Map<String,String> getMapByJson(String responseEntity) {
        Map<String, String> map = new HashMap<String, String>();
        // 阿里巴巴fastjson  将json转换成map
        JSONObject jsonObject = JSONObject.parseObject(responseEntity);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            // 将obj转换成string
            String value = String.valueOf(entry.getValue()) ;
            map.put(key, value);
        }
        return map;
    }
    public static JSONObject getTokenByCode(String redirect_uri, String urlToken, String code, String client_secret,
                                             String client_id, String grant_type) throws IOException {
        HttpPost httpPost = new HttpPost(urlToken);
        List<NameValuePair> request = new ArrayList<>();
        request.add(new BasicNameValuePair("redirect_uri", redirect_uri));
        request.add(new BasicNameValuePair("code", code));
        request.add(new BasicNameValuePair("client_secret", client_secret));
        request.add(new BasicNameValuePair("client_id", client_id));
        request.add(new BasicNameValuePair("grant_type", grant_type));
        httpPost.setEntity(new UrlEncodedFormEntity(request));

        CloseableHttpResponse response = getClient().execute(httpPost);

        try {
            HttpEntity responseEntity = response.getEntity();
            String ret = responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            JSONObject jsonObject = (JSONObject) JSON.parse(ret);
            EntityUtils.consume(responseEntity);
            return jsonObject;
        } finally {
            response.close();
        }
    }
    /**
     * get httpclient
     * @return
     */
    private static CloseableHttpClient getClient() {
        PoolingHttpClientConnectionManager connectionManager = buildConnectionManager("TLSv1.2", new String[]{"TLSv1.2","TLSv1.1"},
                new String[]{"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256","TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA"});
        connectionManager.setMaxTotal(400);
        connectionManager.setDefaultMaxPerRoute(400);
        RequestConfig config =
                RequestConfig.custom().setConnectionRequestTimeout(100).setRedirectsEnabled(false).build();

        return HttpClients.custom()
                .useSystemProperties()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config)
                .build();
    }

    /**
     *
     * @param protocol
     * @param supportedProtocols
     * @param supportedCipherSuites
     * @return
     */
    private static PoolingHttpClientConnectionManager buildConnectionManager(String protocol,
                                                                             String[] supportedProtocols, String[] supportedCipherSuites) {
        PoolingHttpClientConnectionManager connectionManager = null;
        try {
            SSLContext sc = SSLContext.getInstance(protocol);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            sc.init(null, tmf.getTrustManagers(), null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sc, supportedProtocols,
                    supportedCipherSuites, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslsf)
                    .build();
            connectionManager = new PoolingHttpClientConnectionManager(registry);
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }
        return connectionManager;
    }
}

