package com.tingfeng.util.java.extend.orm.hibernate.baseDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tingfeng.util.java.extend.web.bean.MyPage;
import com.tingfeng.util.java.extend.web.bean.MyPager;

/**
 * 
 * @author huitoukest
 * 1.get时返回单个对象;
 * 2.find使用的hql语句进行操作,返回list;
 */
public abstract class BaseDaoImpl extends SuperBaseDaoImpl{
	
	/**
	 * 自动使用page中的信息排序,所以不需要自己排序,
	 * 但是需要给page的排序字段加上表名,例如page.getSort本是"Name",如果使用了表的别名如"User user",改成"user.name"
	 * @param hql
	 * @param params
	 * @param page
	 * @param maxRowsPerPage 每页的最大记录数量,传入null的之后将会默认为前端传入的最大值;
	 * @return
	 */
	
	public <T> List<T> findList(String hql, Map<String, Object> params, MyPage page, Integer maxRowsPerPage) {
		return findList(hql, params,"", page, maxRowsPerPage);
	}
	
	/**
	 * 自动使用page中的信息排序,所以不需要自己排序,
	 * 但是需要给page的排序字段加上表名,例如page.getSort本是"Name",如果使用了表的别名如"User user",改成"user.name"
	 * @param hql
	 * @param params
	 * @param page
	 * @param maxRowsPerPage 每页的最大记录数量,传入null的之后将会默认为前端传入的最大值;
	 * @return
	 */
	
	public <T> List<T> findList(String hql,Map<String, Object> params,String tableAlias,MyPage page,Integer maxRowsPerPage) {
		if(page==null||page.getRows()==null||page.getPage()==null) return this.findList(hql, params);				
		if(maxRowsPerPage!=null&&page.getRows()>maxRowsPerPage) page.setRows(maxRowsPerPage);
		if(tableAlias==null) tableAlias="";
		if(tableAlias.trim().length()>1) tableAlias+=".";
		if(page.getOrder()!=null&&page.getSort()!=null)
		{
			hql=hql.trim()+" order by "+tableAlias+page.getSort()+" "+page.getOrder();
		}
		return this.findList(hql, params, page.getPage(), page.getRows());
	}
	
	/**
	 * 自动使用page中的信息排序,所以不需要自己排序,但是需要给page的排序字段加上表明,例如page.getSort本是"name",改成"user.name"
	 */
	
	public <T> List<T> findList(String hql,MyPage page,Integer maxRowsPerPage) {
		return this.findList(hql, null ,"",page, maxRowsPerPage);
	}
	/**
	 * 自动使用page中的信息排序,所以不需要自己排序,但是需要给page的排序字段加上表明,例如page.getSort本是"name",改成"user.name"
	 */
	
	public <T> List<T> findList(String hql,String tableAlias,MyPage page,Integer maxRowsPerPage) {
		return this.findList(hql, null,tableAlias, page, maxRowsPerPage);
	}
	/**
	 * 只需要传入搜索数据记录的相关hql,其会自动构建相应的Pager,其中hql语句中的from关键字请用小写
	 * @param hql
	 * @param params
	 * @param page
	 * @param maxRowsPerPage
	 * @return 此分页适用于JqueryEasyUI的json格式数据
	 */
	public <T> MyPager<T> findPager(String hql, Map<String, Object> params, MyPage page, Integer maxRowsPerPage){
	        return findPager(hql, params,"", page, maxRowsPerPage);
	}
	
	/**
	 * 只需要传入搜索数据记录的相关hql,其会自动构建相应的Pager,其中hql语句中的from关键字请用小写
	 * @param hql
	 * @param params
	 * @param page
	 * @param maxRowsPerPage
	 * @return 此分页适用于JqueryEasyUI的json格式数据
	 */
	@SuppressWarnings("unchecked")
    public <T> MyPager<T> findPager(String hql,Map<String, Object> params,String tableAlias,MyPage page,Integer maxRowsPerPage){
	        String countHql="select count(*) "+hql.trim().substring(hql.toLowerCase(Locale.ENGLISH).indexOf("from"));	
	        Long count=super.getCountByHql(countHql, params);
			MyPager<T> pager=new MyPager<T>();
	        pager.setTotal(count);
	        ArrayList<T> list=(ArrayList<T>)this.findList(hql, params, tableAlias,page, maxRowsPerPage);
	        pager.setRows(list);
	        return pager;
	}
}

