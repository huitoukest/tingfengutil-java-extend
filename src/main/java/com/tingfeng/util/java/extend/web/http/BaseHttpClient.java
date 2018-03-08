package com.tingfeng.util.java.extend.web.http;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class BaseHttpClient {
    //连接超时时间
    private static final int CONNECTION_TIMEOUT = 50000;//5秒

    //读数据超时时间
    private static final int READ_DATA_TIMEOUT = 180000;//180秒

    private static final String DEFAULT_ENCODING = "UTF-8";

    protected static PoolingHttpClientConnectionManager connManager;

    private static CloseableHttpClient httpClient = null;

    static {
        connManager = new PoolingHttpClientConnectionManager();
        httpClient = HttpClients.custom().setConnectionManager(connManager).build();
    }

    /**
     * sslClient
     */
    public static CloseableHttpClient createSSLClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                    new TrustStrategy() {
                        //信任所有
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws
                                CertificateException {
                            return true;
                        }
                    }).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }


    /**
     * Post请求（默认超时时间，默认编码utf-8）
     *
     * @param url      请求地址
     * @param data     参数
     */
    public static String post(String url, Map<String, Object> data) throws IOException {
        return post(url, CONNECTION_TIMEOUT, READ_DATA_TIMEOUT, data, DEFAULT_ENCODING);
    }

    /**
     * Post请求（默认超时时间）
     *
     * @param url      请求地址
     * @param data     参数
     * @param encoding 编码
     */
    public static String post(String url, Map<String, Object> data, String encoding) throws IOException {
        return post(url, CONNECTION_TIMEOUT, READ_DATA_TIMEOUT, data, encoding);
    }

    public static String post(String url, int timeout, Map<String, Object> data, String encoding) throws IOException {
        return post(url, timeout, timeout, data, encoding);
    }

    /**
     * Post请求
     *
     * @param url            请求地址
     * @param connectTimeout 连接超时
     * @param readTimeout    读取超时
     * @param data           参数
     * @param encoding       编码
     * @throws IOException
     * @throws ParseException
     */
    public static String post(String url, int connectTimeout, int readTimeout, Map<String, Object> data, String encoding) throws IOException {
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            HttpPost post = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(readTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectTimeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);
            if (null != data && !data.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<>();
                for (String key : data.keySet()) {
                    formParams.add(new BasicNameValuePair(key, String.valueOf(data.get(key))));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, encoding);
                post.setEntity(formEntity);
            }

            if (url.startsWith("https")) {//https
                response = createSSLClient().execute(post);
            } else {
                response = httpClient.execute(post);
            }
            entity = response.getEntity();

            if (entity != null) {
                return EntityUtils.toString(entity, encoding);
            }
        } finally {
            if (entity != null) {
                entity.getContent().close();
            }
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 如果失败尝试3次
     *
     * @param url      请求地址
     * @param encoding 编码
     */
    public static String tryGet(String url, String encoding) {
        String resultStr = "";
        for (int i = 0; i < 3; i++) {
            try {
                resultStr = get(url, encoding);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultStr;
    }

    /**
     * Post请求（默认超时时间，默认编码utf-8）
     * @param url      请求地址
     */
    public static String get(String url) throws IOException {
        return get(url, null, CONNECTION_TIMEOUT, READ_DATA_TIMEOUT, DEFAULT_ENCODING);
    }
    /**
     * Post请求（默认超时时间）
     * @param url      请求地址
     * @param encoding 编码
     */
    public static String get(String url, String encoding) throws IOException {
        return get(url, null, CONNECTION_TIMEOUT, READ_DATA_TIMEOUT, encoding);
    }

    public static String get(String url, Map<String, String> cookies, String encoding) throws IOException {
        return get(url, cookies, CONNECTION_TIMEOUT, READ_DATA_TIMEOUT, encoding);
    }

    public static String get(String url, Map<String, String> cookies, int timeout, String encoding) throws IOException {
        return get(url, cookies, timeout, timeout, encoding);
    }

    public static String get(String url, Map<String, String> cookies, int connectTimeout, int readTimeout, String encoding) throws IOException {
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        try {
            HttpGet get = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(readTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectTimeout)
                    .setExpectContinueEnabled(false).build();
            get.setConfig(requestConfig);

            if (cookies != null && !cookies.isEmpty()) {
                StringBuilder buffer = new StringBuilder(128);
                for (String cookieKey : cookies.keySet()) {
                    buffer.append(cookieKey).append("=").append(cookies.get(cookieKey)).append(";");
                }
                //设置cookie内容
                get.setHeader(new BasicHeader("Cookie", buffer.toString()));
            }

            if (url.startsWith("https")) {//https
                response = createSSLClient().execute(get);
            } else {
                response = httpClient.execute(get);
            }

            entity = response.getEntity();

            if (entity != null) {
                return EntityUtils.toString(entity, encoding);
            }
        } finally {
            if (entity != null) {
                entity.getContent().close();
            }
            if (response != null) {
                response.close();
            }
        }
        return null;

    }

    public static String postBody(String url, String body, String encoding) throws IOException {
        return postBody(url,body,encoding,CONNECTION_TIMEOUT, READ_DATA_TIMEOUT);
    }

    /**
     * sslClient
     */
    public static String postBody(String url, String body, String encoding, int connectTimeout, int readTimeout) throws IOException {
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(readTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectTimeout)
                    .setExpectContinueEnabled(false).build();

            post.setConfig(requestConfig);

            if (body != null && !"".equals(body)) {
                StringEntity formEntity = new StringEntity(body, encoding);
                post.setEntity(formEntity);
            }

            if (url.startsWith("https")) {//https
                response = createSSLClient().execute(post);
            } else {
                response = httpClient.execute(post);
            }

            StatusLine line = response.getStatusLine();
            if(line.getStatusCode() != 200){
                return "{\"dataType\":\"OBJECT\",\"isSuccess\":false,\"resultDesc\":\"请求地址不存在！\",\"resultCode\":\"E40000004\"}";
            }

            entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, encoding);
            }
        } finally {
            if (entity != null) {
                entity.getContent().close();
            }
            if (response != null) {
                response.close();
            }
        }

        return null;

    }

    /**
     * map转成queryStr
     *
     * @param paramMap 参数列表
     */
    public static String mapToQueryStr(Map<String, String> paramMap) {
        StringBuilder strBuff = new StringBuilder();
        for (String key : paramMap.keySet()) {
            strBuff.append(key).append("=").append(paramMap.get(key)).append("&");
        }
        return strBuff.substring(0, strBuff.length() - 1);
    }

    public static String post(String serverUrl, String fileParamName, File file, Map<String, Object> params)
            throws ClientProtocolException, IOException {
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        try{
        HttpPost httpPost = new HttpPost(serverUrl);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 上传的文件
        //builder.addBinaryBody(fileParamName, file);
        FileBody fileBody = new FileBody(file, ContentType.create(getContentType(file.getName())), file.getName());
        builder.addPart(fileParamName, fileBody);
        // 设置其他参数
        if(params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if(entry.getValue() != null) {
                    builder.addTextBody(entry.getKey(),entry.getValue().toString() , ContentType.TEXT_PLAIN.withCharset("UTF-8"));
                }
            }
        }
        entity = builder.build();
        httpPost.setEntity(entity);
        response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
                StringBuffer buffer = new StringBuffer();
                String str = "";
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                return buffer.toString();
            }
            return null;
        }finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static String getContentType(String filePath){
        Path path = Paths.get(filePath);
        String contentType = "application/octet-stream";
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentType;
    }
}
