package com.tingfeng.util.java.extend.web.http;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.tingfeng.util.java.base.common.utils.string.StringUtils;


public class HttpUtils {
    
	public static String doGet(String url,Map<String,Object> params){
        String urlTmp = StringUtils.getGetUrl(url,params);
        return  doGet(urlTmp);
    }

    public static String doGet(String url){
        try {
            return BaseHttpClient.get(url);
        } catch (IOException e) {
            throw new RuntimeException("request error!" + e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("unknown exception!", ex);
        }
    }

    public static String doPost(String url, Map<String, Object> data){
        try {
            return BaseHttpClient.post(url, data);
        } catch (IOException e) {
            throw new RuntimeException("request error!" + e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("unknown exception!", ex);
        }
    }
    public static String doPost(String url, String fileParamName, File file){
        return doPost(url,fileParamName,file,null);
    }
    public static String doPost(String url, String fileParamName, File file, Map<String, Object> params){
        try {
            return BaseHttpClient.post(url,fileParamName,file,params);
        } catch (IOException e) {
            throw new RuntimeException("request error!" + e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("unknown exception!", ex);
        }
    }

}
