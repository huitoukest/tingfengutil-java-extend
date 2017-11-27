package com.tingfeng.util.java.extend.web.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import com.tingfeng.util.java.base.common.utils.RandomUtils;

@SuppressWarnings("deprecation")
public class HttpUtils {
	
	
	/**
	 * map中包含文件对象,将会以Base64的方式发送数据 如果map中一个文件都没有,将会报错;
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String uploadFileToWebServiceClient(String url,
			Map<String,?> params) throws IOException {
		FileInputStream fin = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		
		String uuidString = RandomUtils.getRandomLong() + "";
		int bufferSize = 1024 * 5;
		byte[] buffer = new byte[bufferSize];
		String fileKeyName = "";
		Map<String, String> httpParams = new HashMap<String, String>();
		Set<String> paramKeys = params.keySet();
		String value = null;
		for (String pString : paramKeys) {
			if (params.get(pString) != null) {
				if (params.get(pString) instanceof File) {
					File file = (File) params.get(pString);
					fin = new FileInputStream(file);
					value = file.getPath();
					fileKeyName = pString;
				} else {
					value = params.get(pString).toString();
				}
			}
			httpParams.put(pString, value);
		}
		if (fin != null) {
			// 从文件读取数据至缓冲区
			while (fin.read(buffer, 0,bufferSize) != -1) {
				String bufferStr = Base64.encodeBase64String(buffer);
				httpParams.put(fileKeyName, bufferStr);
				List<NameValuePair> paramList = getNameValuePairsByMap(httpParams);
				paramList.add(new BasicNameValuePair(fileKeyName,bufferStr));
				paramList.add(new BasicNameValuePair("complate", "0"));
				paramList.add(new BasicNameValuePair("uid", uuidString));			
				String result = postHttpRequest(paramList, url,client,httpPost,2);
				if (result == null) {
					return null;
				}
			}
			httpParams.put(fileKeyName, "");
		}
		List<NameValuePair> paramList = getNameValuePairsByMap(httpParams);
		paramList.add(new BasicNameValuePair("complate", "1"));
		paramList.add(new BasicNameValuePair("uid", uuidString));
		String result = postHttpRequest(paramList, url,client,httpPost,2);
		
		return result;
	}

	private static List<NameValuePair> getNameValuePairsByMap(
			Map<String, String> maps) {
		/*Set<String> keys = maps.keySet();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (String key : keys) {
			nameValuePairs.add(new BasicNameValuePair(key, maps.get(key)));
		}*/
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("createTime", "2015-1-1"));
		paramList.add(new BasicNameValuePair("title", "测试"));
		paramList.add(new BasicNameValuePair("content", "323232试"));
		paramList.add(new BasicNameValuePair("reserveName",
				"11111111111试"));
		paramList.add(new BasicNameValuePair("reserveId", "3232"));
		paramList.add(new BasicNameValuePair("lon", "100"));
		paramList.add(new BasicNameValuePair("lat", "27"));
		paramList
				.add(new BasicNameValuePair("uploadUserName", "admin"));
		paramList.add(new BasicNameValuePair("uploadUserId", "111"));
		return paramList;
	}
	/**
	 * postTryCount即其默认发送失败之后再次发送的次数,最小为0;
	 * @param paramList
	 * @param url
	 * @param client
	 * @param httpPost
	 * @param postTryCount
	 * @return
	 */
	private static String postHttpRequest(List<NameValuePair> paramList,
			String url,HttpClient client,HttpPost httpPost,int postTryCount) {
		HttpResponse response = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
			// request time out（这个是请求超时时间）
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT,1000*100);
			// read time out（这个是读取数据超时时间）
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					1000*600);
			response = client.execute(httpPost);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// 判断请求是否成功
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			return response.getEntity().toString();
		} else {
			if(postTryCount>0){
				return postHttpRequest(paramList, url, client, httpPost, postTryCount-1);
			}
			return null;
		}
	}

}
