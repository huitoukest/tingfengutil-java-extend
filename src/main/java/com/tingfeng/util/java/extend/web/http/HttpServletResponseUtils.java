package com.tingfeng.util.java.extend.web.http;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.tingfeng.util.java.base.common.utils.string.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

public class HttpServletResponseUtils {
	/**
	 * 发送纯String文本
	 * @param response
	 * @param s
	 * @throws IOException
	 */
		public static void sendText(HttpServletResponse response, String s)
				throws IOException {
			if(s==null)
				return;
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.getWriter().write(s);
			response.getWriter().flush();
			response.getWriter().close();
		}
		public static void sendText(HttpServletResponse response,Object obj)
				throws IOException {
			if(obj==null)
				return;
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			String s=JSON.toJSONString(obj);
			response.getWriter().write(s);
			response.getWriter().flush();
			response.getWriter().close();
		}
		public static void sendText(HttpServletResponse response,Object obj,SerializerFeature feature)
				throws IOException {
			if(obj==null)
				return;
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			String s=JSON.toJSONString(obj,feature);
			response.getWriter().write(s);
			response.getWriter().flush();
			response.getWriter().close();
		}
		public static void sendText(HttpServletResponse response,List<?> obj)
				throws IOException {
			if(obj==null)
				return;
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			String s=JSON.toJSONString(obj);
			response.getWriter().write(s);
			response.getWriter().flush();
			response.getWriter().close();
		}

	/**
	 * 将一个String发送
	 * @param response
	 * @param s
	 * @param ContentType 内容的类型text/json等
	 * @param Header 头文件
	 * @param cache  有无cache
	 * @param charEncoding 编码
	 * @throws IOException
	 */
		public static void sendText(HttpServletResponse response, String s,String ContentType, String Header, String cache,String charEncoding) throws IOException {
			if (StringUtils.isNotEmpty(charEncoding))
				response.setCharacterEncoding(charEncoding);
			if (StringUtils.isNotEmpty(ContentType))
				response.setContentType(ContentType);
			if (StringUtils.isNotEmpty(Header) && StringUtils.isNotEmpty(cache))
				response.setHeader(Header, cache);
			response.getWriter().write(s);
			response.getWriter().flush();
			response.getWriter().close();
		}
		/**
		 * 过滤掉不需要的字段,new MyPropertyFilter();这个是自己写的一个filter
	     * @throws IOException
		 * @param response
		 * @param object
		 * @param filter
		 * @throws IOException
		 */
		public static void sendToTextWithoutThing(HttpServletResponse response, Object object,PropertyFilter filter) throws IOException
		{
	        SerializeWriter sw = new SerializeWriter();  
	        JSONSerializer serializer = new JSONSerializer(sw);  
	        serializer.getPropertyFilters().add(filter);  
	        response.setCharacterEncoding("UTF-8");
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			String string;
			string= JSON.toJSONString(object,filter);
	        response.getWriter().write(string);
	        response.getWriter().flush();
			response.getWriter().close();
		}
		
	/**
	 * 将一个对象以Json发送
	 * @param response
	 * @param object 一个java类对象
	 * @param filter 保留的需要的字段new SimplePropertyPreFilter(实体类.class,"字段名","字段名".....),或者(""字段名","字段名".....")
	 * @throws IOException
	 */
		@SuppressWarnings("unchecked")
        public static void sendToTextWithTing(HttpServletResponse response, Object object,SimplePropertyPreFilter filter)
				throws IOException {
			if(object instanceof List)//如果是list类型,调用其他方法;
				sendList(response, (List<? extends Object>) object,filter);
			String s;
			if(filter!=null)
			s = JSON.toJSONString(object,filter);
			else  s = JSON.toJSONString(object);		
			//JSON json=JSON.parseObject(s);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");		
			//JSON.writeJSONStringTo(json, response.getWriter());		
			response.getWriter().write(s);
			response.getWriter().flush();
			response.getWriter().close();
		}
		/**
		 * 将一个List对象以json的方式发送
		 * @param response
		 * @param object
		 * @throws IOException
		 */
		private static void sendList(HttpServletResponse response, List<? extends Object> list,SimplePropertyPreFilter filter) throws IOException
		{
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			String s;
			if(filter!=null)
				s=JSON.toJSONString(list,filter);
			//jsonArray=JSONArray.parseArray(JSON.toJSONString(list,filter));
			else  
				//jsonArray=JSONArray.parseArray(JSON.toJSONString(list));
			s=JSON.toJSONString(list);
			//JSONArray.writeJSONStringTo(jsonArray, response.getWriter());
					response.getWriter().write(s);
	     	response.getWriter().flush();
			response.getWriter().close();
		}		
}
