package com.tingfeng.util.java.extend.web.bean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;

/**
 * 
 * @author huitoukest
 * 后台用来装载数据之后往前台发送用的包装类,适用于easuUI
 * @param <T>
 * 
 */
public class MyPager<T>{
	/**
	 * 一个ArrayList的行数据集合
	 */
private ArrayList<T> rows=new ArrayList<T>(0);
/**
 * 当前总的记录数量,指的是数据库中一共有多少条数据
 */
private Long total=0L;
	/**
	 * 
	 * @param rows 一个ArrayList的行数据集合
	 * @param total 当前总的记录数量,指的是数据库中一共有多少条数据
	 */
	public MyPager(ArrayList<T> rows,Long total) {
		this.rows=rows;
		this.total=total;
	}
	public MyPager() {
	}
	public ArrayList<T> getRows() {
		return rows;
	}
	public Long getTotal() {
		return total;
	}
	public void setRows(ArrayList<T> rows) {
		this.rows = rows;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	/**
	 * 将自己的内容复制一份出来,这样来防止json序列化的时候,
	 * 将不必要的数据库内容被拷贝了出来
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 */
    public MyPager<T> copyMySelf(Class<T> cls) throws InstantiationException, IllegalAccessException, InvocationTargetException{
    	ArrayList<T> rr=new ArrayList<T>();
    	for(T src:rows){
    		T des=cls.newInstance();
    		BeanUtils.copyProperties(des,src);
    		rr.add(src);
    	}
    	MyPager<T> p=new MyPager<T>(rr,this.total);
    	return p;
    }
}
