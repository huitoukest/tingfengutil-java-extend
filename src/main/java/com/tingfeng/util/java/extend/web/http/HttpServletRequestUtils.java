package com.tingfeng.util.java.extend.web.http;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.tingfeng.util.java.base.common.constant.ObjectType;
import com.tingfeng.util.java.base.common.utils.ObjectTypeUtils;
import com.tingfeng.util.java.base.common.utils.reflect.ReflectJudgeUtils;
import com.tingfeng.util.java.base.common.utils.reflect.ReflectUtils;



public class HttpServletRequestUtils {
	public static String getServerPath(HttpServletRequest request)
	{
		String path = request.getContextPath();
		return path;
	}
	public static String getServerBasePath(HttpServletRequest request){
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
		return basePath+getServerPath(request);
	}
	public static Byte getByte(String key,HttpServletRequest request){
		String value=request.getParameter(key);
		if(value==null)
			return null;
		return Byte.parseByte(value);
	}
	public static Short getShort(String key,HttpServletRequest request){
		String value=request.getParameter(key);
		if(value==null)
			return null;
		return Short.parseShort(value);
	}
	public static Integer getInteger(String key,HttpServletRequest request){
		String value=request.getParameter(key);
		if(value==null)
			return null;
		return Integer.parseInt(value);
	}
	public static Long getLong(String key,HttpServletRequest request){
		String value=request.getParameter(key);
		if(value==null)
			return null;
		return Long.parseLong(value);
	}
	public static Boolean getBoolean(String key,HttpServletRequest request){
		String value=request.getParameter(key);
		if(value==null)
			return null;
		return Boolean.parseBoolean(value);
	}
	
	public static Float getFloat(String key,HttpServletRequest request){
		String value=request.getParameter(key);
		if(value==null)
			return null;
		return Float.parseFloat(value);
	}
	
	public static Double getDouble(String key,HttpServletRequest request){
		String value=request.getParameter(key);
		if(value==null)
			return null;
		return Double.parseDouble(value);
	}
	
	public static String getString(String key,HttpServletRequest request){
		return request.getParameter(key);
	}
	
	
	public static Byte[] getBytes(String key,HttpServletRequest request){
		String[] value=request.getParameterValues(key);
		if(value==null)
			return null;
		Byte[] arr=new Byte[value.length];
		for(int i=0;i<value.length;i++){
			String string=value[i];
			arr[i]=Byte.parseByte(string);
		}
		return arr;
	}
	public static Short[] getShorts(String key,HttpServletRequest request){
		String[] value=request.getParameterValues(key);
		if(value==null)
			return null;
		Short[] arr=new Short[value.length];
		for(int i=0;i<value.length;i++){
			String string=value[i];
			arr[i]=Short.parseShort(string);
		}
		return arr;
	}
	public static Integer[] getIntegers(String key,HttpServletRequest request){
		String[] value=request.getParameterValues(key);
		if(value==null)
			return null;
		Integer[] arr=new Integer[value.length];
		for(int i=0;i<value.length;i++){
			String string=value[i];
			arr[i]=Integer.parseInt(string);
		}
		return arr;
	}
	public static Long[] getLongs(String key,HttpServletRequest request){
		String[] value=request.getParameterValues(key);
		if(value==null)
			return null;
		Long[] arr=new Long[value.length];
		for(int i=0;i<value.length;i++){
			String string=value[i];
			arr[i]=Long.parseLong(string);
		}
		return arr;
	}
	public static Boolean[] getBooleans(String key,HttpServletRequest request){
		String[] value=request.getParameterValues(key);
		if(value==null)
			return null;
		Boolean[] arr=new Boolean[value.length];
		for(int i=0;i<value.length;i++){
			String string=value[i];
			arr[i]=Boolean.parseBoolean(string);
		}
		return arr;
	}
	
	public static Float[] getFloats(String key,HttpServletRequest request){
		String[] value=request.getParameterValues(key);
		if(value==null)
			return null;
		Float[] arr=new Float[value.length];
		for(int i=0;i<value.length;i++){
			String string=value[i];
			arr[i]=Float.parseFloat(string);
		}
		return arr;
	}
	
	public static Double[] getDoubles(String key,HttpServletRequest request){
		String[] value=request.getParameterValues(key);
		if(value==null)
			return null;
		 Double[] arr=new Double[value.length];
		for(int i=0;i<value.length;i++){
			String string=value[i];
			arr[i]=Double.parseDouble(string);
		}
		return arr;
	}
	
	public static String[] getStrings(String key,HttpServletRequest request){
		return request.getParameterValues(key);
	}
	
	/**
	 * 默认传入的参数值应该是一个毫秒数
	 * @param key
	 * @param request
	 * @return
	 */
	public static java.util.Date getDate(String key,HttpServletRequest request){
		Long value=getLong(key, request);		
		if(value==null)
			return null;
		return new Date(value);
	}
	
	/**
	 * 默认传入的参数值应该是一个毫秒数或者字符串时间,会自动转换
	 * @param key
	 * @param fromatString 但是需要指定转换的格式如"yyyy-MM-dd HH:mm:ss"
	 * @param request
	 * @return
	 * @throws ParseException 
	 */
	public static java.util.Date getDate(String key,String fromatString,HttpServletRequest request) throws ParseException{	
		try{
			Long value=getLong(key, request);	
			if(value==null)
				return null;
			return new Date(value);
		}catch(Exception e){
			SimpleDateFormat format = new SimpleDateFormat(fromatString);
			//设置时间
			format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			String value=getString(key, request);
			if(value==null) return null;
			Date date =format.parse(value);
			return date;
		}			
	}
	
	/**
	 * 目前只支持基础数据类型
	 * @param cls
	 * @param prefix
	 * @param request
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("deprecation")
	public static <T> T getBeanFromRequest(Class<T> cls,String prefix,HttpServletRequest request) throws InstantiationException, IllegalAccessException{
		Field[] fs=cls.getDeclaredFields();
		T t=cls.newInstance();
		for(Field f:fs){
			try{
			 f.setAccessible(true);//强制获取,设置值
			 ObjectType type=ObjectTypeUtils.getObjectType(f);
			 String name=f.getName();
			 String obj=request.getParameter(prefix+name);
			//if(obj==null)
			//	  obj="null";
			 //忽略静态属性
			 if(ReflectJudgeUtils.isStaticField(f))
				 continue;
			//按照基础六类属性来处理
			 switch (type) {
				case Boolean:
					if(!ReflectUtils.setter(t, name,Boolean.parseBoolean(obj), Boolean.class))
					f.set(t,Boolean.parseBoolean(obj));
					break;
				case Float:
					if(!ReflectUtils.setter(t, name,Float.parseFloat(obj),Float.class))
					 f.set(t,Float.parseFloat(obj));
					break;
				case Double:
					if(!ReflectUtils.setter(t, name,Double.parseDouble(obj), Double.class))
					 f.set(t,Double.parseDouble(obj));
					break;
				case Long:
					if(!ReflectUtils.setter(t, name,Long.parseLong(obj), Long.class))
					 f.set(t,Long.parseLong(obj));
					break;
				case Integer:
					if(!ReflectUtils.setter(t, name,Integer.parseInt(obj), Integer.class))
					 f.set(t,Integer.parseInt(obj));
					break;
				case Short:
					if(!ReflectUtils.setter(t, name,Short.parseShort(obj), Short.class))
					 f.set(t,Short.parseShort(obj));
					break;
				case Byte:
					if(!ReflectUtils.setter(t, name,Byte.parseByte(obj), Byte.class))
					 f.set(t,Byte.parseByte(obj));
					break;
				case String:
					if(!ReflectUtils.setter(t, name,obj, String.class))
					 f.set(t,obj);
					break;
				case Date:
					Date date=null;
					if(obj!=null)
						date=new Date(Date.parse(obj));
					if(!ReflectUtils.setter(t, name,date, Date.class))
					f.set(t,date);
					break;
			default:				
				Object fObj=getBeanFromRequest(f.getClass(), prefix+name+".", request);   
				if(fObj==null) 
					continue;
				if(!ReflectUtils.setter(t, name, fObj, fObj.getClass()))
					f.set(t, fObj);
				break;
			 }	    
			} catch (Exception e) {			
				//e.printStackTrace();
			    continue;
			}//end try
		}//end for
		return t;
	}
   
}
