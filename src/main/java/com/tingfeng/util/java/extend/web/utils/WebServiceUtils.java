package com.tingfeng.util.java.extend.web.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.net.util.Base64;

import com.tingfeng.util.java.base.common.inter.PercentActionCallBackI;
import com.tingfeng.util.java.base.common.utils.RandomUtils;

public class WebServiceUtils {
	public static int http_connection_timeout = 60 * 1000;
	/**
	 * 当进度编号大于updateCallBadcSpace%的时候,调用回调函数,默认0.5%
	 */
	public static double updateCallBadcSpace=0.5;
    /**
     * SOAP 1.2规范
     * @param namespace
     * @param methodName
     * @param patameterMap
     * @return
     */
	private static String buildRequestData(String namespace, String methodName,
			Map<String, String> patameterMap) {
		StringBuffer soapRequestData = new StringBuffer();
		soapRequestData.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soapRequestData
				.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ns=\""
						+ namespace + "\">");
		soapRequestData.append("<soap:Header/><soap:Body>");
		boolean hasPatameter = false;
		StringBuffer patameterString = new StringBuffer();
		if (patameterMap != null) {
			Set<String> nameSet = patameterMap.keySet();
			if (nameSet.size() > 0) {
				hasPatameter = true;
			}
			for (String name : nameSet) {
				String value=patameterMap.get(name);
				patameterString.append("<ns:" + name + ">"+ value+ "</ns:" + name + ">");
			}
		}
		if (!hasPatameter) {
			soapRequestData.append("<ns:" + methodName + "/>");
		} else {
			soapRequestData.append("<ns:" + methodName + ">" + patameterString+ "</ns:" + methodName + ">");
		}
		soapRequestData.append("</soap:Body>");
		soapRequestData.append("</soap:Envelope>");
		return soapRequestData.toString();
	}

	/**
	 * 
	 * @param host
	 * @param wsdlUrl
	 * @param namespace
	 * @param methodName
	 * @param params 键值对中包含一个文件,有且只能有一个文件;
	 * @param callBack
	 * @return
	 */
	public static String uploadFileToWebService(String host, String wsdlUrl,
			String namespace, String methodName, Map<String, ?> params,
			PercentActionCallBackI<File> callBack) {
		String result = "";
		File file = null;
		try {
			FileInputStream fin = null;			
			String uuidString = RandomUtils.getRandomLong() + "";
			int bufferSize =1024 *500;
			byte[] buffer = new byte[bufferSize];
			String fileKeyName = "";
			Map<String, String> httpParams = new HashMap<String, String>();
			Set<String> paramKeys = params.keySet();
			String value = null;
			for (String pString : paramKeys) {
				if (params.get(pString) != null) {
					if (params.get(pString) instanceof File) {
						file = (File) params.get(pString);
						fin = new FileInputStream(file);
						value = file.getPath();
						fileKeyName = pString;
					} else {
						value = params.get(pString).toString();
					}
				}
				httpParams.put(pString, value);
			}
			if(callBack!=null){
				callBack.updateRate(0);
			}
			if(file==null||fin==null)
				return postToWebService(host, wsdlUrl, namespace,methodName, httpParams, 2);
			httpParams.put("complate", "0");
			httpParams.put("uid", uuidString);
			int postCount=1;
			double lastPercent=0d;
			if (fin != null) {
				// 从文件读取数据至缓冲区
				while (fin.read(buffer, 0, bufferSize) != -1) {
					String bufferStr = Base64.encodeBase64String(buffer);
					httpParams.put(fileKeyName, bufferStr);
					result=postToWebService(host, wsdlUrl, namespace,methodName, httpParams, 2);
					if (result == null||result.length()<1) {
						return getSoapReturn(result,methodName);
					}
					double percent=(double)(100.0*postCount++*bufferSize/file.length());
					if(callBack!=null&&percent-lastPercent>=updateCallBadcSpace){
						callBack.updateRate(percent>100?100:percent);
						lastPercent=percent;
					}	
				}
				httpParams.put(fileKeyName, "");
			}
			httpParams.put(fileKeyName, "");
			httpParams.put("complate", "1");
			httpParams.put("uid", uuidString);
			result = postToWebService(host, wsdlUrl, namespace, methodName,
					httpParams, 2);
			//Log.Info("上传文件成功!", result == null ? "" : result);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return result;
	}

	/**
	 * 返回xml中<return>标签中的内容</return>
	 * 
	 * @return
	 */
	public static String getSoapReturn(String result,String methodName) {

		String s =(result==null?"":result);
		int start = s.indexOf("<" + methodName + "Result>");
		int end = s.lastIndexOf("</" + methodName + "Result>");
		if (start >= 0 && end >= 0) {
			start = start + ("<" + methodName + "Result>").length();
			s = s.substring(start, end);
			if (s.equals(""))
				s = "";
		}

		return s;
	}
	/**
	 * 
	 * @param conn
	 *            HttpURLConnection,一个连接好的Http连接,并且此方法只是post数据,不管理此连接的生命周期
	 * @param wsdlUrl
	 * @param namespace
	 * @param methodName
	 * @param httpParams
	 *            发送的内容的键值对
	 * @param tryCount
	 *            失败后重新尝试的次数
	 * @return
	 */
	public static String postToWebService(String host, String wsdlUrl,
			String namespace, String methodName,
			Map<String, String> httpParams, int tryCount) {
		String result = null;
		int code = 0;
		try {
			URL urlTemp = new URL(wsdlUrl);
			HttpURLConnection conn = (HttpURLConnection) urlTemp
					.openConnection();
			String contentType;
			contentType = "application/soap+xml; charset=utf-8";
			String requestData = buildRequestData(namespace, methodName,
					httpParams);
			byte[] bytes = requestData.getBytes("utf-8");
			String contentLength = bytes.length + "";
			/* 设定传送的method=POST */
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(http_connection_timeout * 5);
			// read time out（这个是读取数据超时时间）
			conn.setUseCaches(false);
			/* setRequestProperty */
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setRequestProperty("Content-Type", contentType);
			conn.setRequestProperty("Content-Length", contentLength);
			conn.setRequestProperty("Host", host);
			conn.setRequestProperty("Connection", "Keep-Alive");
			// conn.setRequestProperty("User-Agent",
			// "Apache-HttpClient/4.1.1 (java 1.5)");
			/* 允许Input、Output，不使用Cache */
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();
			/* 设定DataOutputStream */
			OutputStream os = conn.getOutputStream(); // exception throws here!
			DataOutputStream dataout = new DataOutputStream(os);
			dataout.write(bytes);
			dataout.flush();
			dataout.close();
			os.close();
			code = conn.getResponseCode(); // 用来获取服务器响应状态
			if (code == HttpURLConnection.HTTP_OK) {
			} else {
				if (tryCount > 2) {
					return postToWebService(host, wsdlUrl, namespace,
							methodName, httpParams, tryCount - 1);
				}
				return null;
			}
			/* 取得Response内容 */
			InputStream is = conn.getInputStream();
			result = transInputStreamToStringByEncode(is, "UTF-8");
			is.close();
			conn.disconnect();
			//Log.Info("上传文件成功!" + tryCount, result == null ? "" : result);
		} catch (Exception e) {
			//Log.Info("上传文件失败" + tryCount, e);
			if (tryCount > 0) {
				result = postToWebService(host, wsdlUrl, namespace, methodName,
						httpParams, tryCount - 1);
			}
		}
		return getSoapReturn(result,methodName);
	}

	/**
	 * 用指定的写出文件流来写出文件;
	 * 
	 * @param file
	 * @param os
	 * @param callBack
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static void writeFile(File file, OutputStream os,
			PercentActionCallBackI<File> callBack) throws Exception {
		/* 取得文件的FileInputStream */
		FileInputStream fStream = new FileInputStream(file);
		/* 设定每次写入4096bytes */
		int bufferSize = 4096;
		byte[] buffer = new byte[bufferSize];
		int lengthPerTime = 0;// 循环读写中,每一次读取的字节
		int lengthReadSum = 0;// 循环读写中,读取的字节的总数量
		Long fileSize = file.length();// 得到文件的总长度
		int countOfUpdate = (int) (fileSize / bufferSize / 100);
		if (countOfUpdate == 0) {
			countOfUpdate = 1;
		}
		int countOfNowCycle = 0;// 当前循环的次数
		/* 从文件读取数据到缓冲区 */
		while ((lengthPerTime = fStream.read(buffer)) != -1) {
			/* 将数据写入DataOutputStream中 */
			try {
				os.write(buffer, 0, lengthPerTime);
				// os.flush();
			} catch (Exception e) {
				throw e;
			}
			lengthReadSum += lengthPerTime;
			if (countOfNowCycle >= countOfUpdate) {
				countOfNowCycle = 0;
				if (callBack != null)
					callBack.updateRate(lengthReadSum / fileSize);
			}
			countOfNowCycle++;
		}
		os.flush();
		fStream.close();

	}

	/**
	 * 将一个输入流转换成指定编码的字符串
	 * 
	 */
	private static String transInputStreamToStringByEncode(
			InputStream inputStream, String encode) {
		// 内存流
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[4096];
		int len = 0;
		String result = null;
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);
				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
