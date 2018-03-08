package com.tingfeng.util.java.extend.orm.hibernate.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

/**
 * 
 * @author huitoukest
 * 通过HQL语句操作tree的工具类,此类中的方法不会管理相关事务,
 * 操作的时候,如果父节点是一个对象,可用parent.id的形式出入hql语句;
 */
@SuppressWarnings("unchecked")
public class TreeHelper<T>{
	/**
	 * 排序方式,升序还是降序
	 * @author huitoukest
	 *
	 */
	public enum OrderType{
		DESC("desc"),ASC("asc");
		private String typeString;		
		private OrderType(String type){
			this.typeString=type;
		}
		public String getTypeValue(){
			return this.typeString;
		}
	}
	/**
	 * 节点的类型1表示父节点,2表示子节点,3表示所有节点,默认为3
	 * @author huitoukest
	 *
	 */
	public enum NodeType{
		Parent(1),Leaf(2),All(3);
		private Integer type=3;		
		private NodeType(Integer type){
			this.type=type;
		}
		public Integer getTypeValue(){
			return this.type;
		}
	}
	/**
	 * 
	 * @author huitoukest
	 * 根据传入的entity,返回一个树形结构
	 * @param <T>
	 */
	public interface HibernateTreeI<E>{
		public Object getId(E t);
		public Object getPid(E t);		
	}
	
	    private String tableNameString=null;
		private String pidName;
		private String idName;
		//private Class<T> cls;
		private HibernateTreeI<T> tree;
		public TreeHelper(HibernateTreeI<T> tree,String tableName,String pidName,String idName) {
               this.tableNameString=tableName;
               this.pidName=pidName;
               this.idName=idName;
               this.tree=tree;
		}
		/**
		 * 需要注意的是,这里使用的hql语句
		 * @param session hibernate的session
		 * @param order 拍下方式,升序/降序
		 * @param sortColumns 排序使用的字段名称
		 * @return 指定父节点下面的,下一级的子节点列表;
		 */
		@SuppressWarnings("unchecked")
        public List<T> getSubTreeByPid(Session session,Object pidValue,OrderType orderType,String...sortColumns) {
			String hql = null;
			if(pidValue==null)
			{
				hql="select table_ from "+tableNameString+" table_ where table_."+pidName+" is null";
			} else{
				hql="select table_ from "+tableNameString+" table_ where table_."+pidName+"=:pv";
			}
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("pv", pidValue);
			hql+=getOrderSortHqlString(orderType, sortColumns);			
			return (List<T>) this.findList(session, hql, params);			
		}
		
		public T getNodeById(Session session,Object idValue,OrderType orderType,String hqlSelectString,String...sortColumns) {
			String hql = null;
			if(idValue==null)
			{
				hql="select table_ from "+tableNameString+" table_ where table_."+idName+" is null";
			} else{
				hql="select table_ from "+tableNameString+" table_ where table_."+idName+"=:id";
			}
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("id", idValue);
			if(hqlSelectString!=null){
				hql+=hqlSelectString;
			}
			hql+=getOrderSortHqlString(orderType, sortColumns);			
			return findOne(session, hql, params);	
		}
		/**
		 * 得到指定节点的子节点集合
		 * @param session
		 * @param pids a,b,c这种以逗号分隔的值
		 * @param orderType
		 * @param sortColumns
		 * @return
		 */
		@SuppressWarnings("unchecked")
        public List<T> getSubNodeByPids(Session session,List<Object> pids,OrderType orderType,String hqlSelectString,String...sortColumns) {
			if(pids==null||pids.isEmpty())
				return new ArrayList<T>();
			String hql = null;
				hql="select table_ from "+tableNameString+" table_ where table_."+pidName+" in (:pvs)";		
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("pvs",pids);
			if(hqlSelectString!=null)
				hql+=hqlSelectString;
			hql+=getOrderSortHqlString(orderType, sortColumns);	
			List<T> list=(List<T>) this.findList(session, hql, params);	
			if(list==null){
				list=new ArrayList<T>();
			}				
			return 		list;
		}
		/**
		 * 得到指定节点的子节点的父节点的列表,可以理解为是对传入的有子节点的指定节点的过滤保留
		 * @param session
		 * @param pids
		 * @param orderType
		 * @param sortColumns
		 * @return
		 */
		@SuppressWarnings("unchecked")
        public List<Object> getSubPidsByPids(Session session,List<Object> pids,OrderType orderType,String hqlSelectString,String...sortColumns) {
			if(pids==null||pids.isEmpty())
				return new ArrayList<Object>();
			String hql = null;
				hql="select distinct table_."+pidName+" from "+tableNameString+" table_ where table_."+pidName+" in (:pv)";		
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("pv",pids);
			if(hqlSelectString!=null)
				hql+=hqlSelectString;
			hql+=getOrderSortHqlString(orderType, sortColumns);	
			List<Object> objs=(List<Object>) this.findList(session, hql, params);			
		    if(objs==null)
		    {
		    	objs=new ArrayList<Object>();
		    }
			return objs;
		} 
		/**
		 * 得到指定节点的子节点的子节点的列表
		 * @param session
		 * @param pids
		 * @param orderType
		 * @param sortColumns
		 * @return
		 */
		@SuppressWarnings("unchecked")
        public List<Object> getSubIdsByPids(Session session,List<Object> pids,OrderType orderType,String hqlSelectString,String...sortColumns) {
			if(pids==null||pids.isEmpty())
				return new ArrayList<Object>();
			String hql = null;
				hql="select distinct table_."+idName+" from "+tableNameString+" table_ where table_."+pidName+" in (:pv)";		
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("pv",pids);
			if(hqlSelectString!=null)
				hql+=hqlSelectString;
			hql+=getOrderSortHqlString(orderType, sortColumns);	
			List<Object> objs=(List<Object>) this.findList(session, hql, params);			
		    if(objs==null)
		    {
		    	objs=new ArrayList<Object>();
		    }
			return objs;
		} 
		
		
		/**
		 * 
		 * @param session
		 * @param pidValue
		 * @param orderType
		 * @param hqlSelectString 自定义搜索条件,实体使用table_替代,如" and table_.status=1";
		 * @param sortColumns
		 * @return
		 */
		@SuppressWarnings("unchecked")
        public List<T> getSubTreeByPid(Session session,Object pidValue,OrderType orderType,String hqlSelectString,String...sortColumns) {
			String hql = null;
			if(pidValue==null)
			{
				hql="select table_ from "+tableNameString+" table_ where table_."+pidName+" is null";
			} else{
				hql="select table_ from "+tableNameString+" table_ where table_."+pidName+"=:pv";
			}
			if(hqlSelectString!=null)
			hql+=hqlSelectString;
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("pv", pidValue);
			hql+=getOrderSortHqlString(orderType, sortColumns);			
			return (List<T>) this.findList(session, hql, params);			
		}
		
		/**
		 * 得到所有的父节点;
		 * @param hqlSelectString 自定义搜索条件,实体使用table_替代,如 " and table_.status=1";
		 * @return
		 */
        public List<Object> getAllPids(Session session,String hqlSelectString){
			String hqlString="select distinct table_."+pidName+" from "+tableNameString+" table_ where 1=1";
			if(hqlSelectString!=null)
				hqlString+=hqlSelectString;
			List<Object> list=(List<Object>) this.findList(session, hqlString,null);
			return list;
		}
		/**
		 * 得到排序的hql片段语句
		 * @param orderType
		 * @param sortColumns
		 * @param params 向此params中增加排序的相关参数
		 * @return
		 */
		private String getOrderSortHqlString(OrderType orderType,String...sortColumns){
			String hql="";
			if(sortColumns==null)
				sortColumns=new String[]{idName};
			if(sortColumns!=null){
				hql=" order by";
				for(int i=0;i<sortColumns.length;i++){
					String s=sortColumns[i];
					if(i>0){
						hql+=",";
					}
					hql+=" table_."+s;
					
				}
			}
			if(orderType!=null)
			hql+=" "+orderType.getTypeValue();
			return hql;
		}
	
	/**
	 * 根据父id,得到子节点的数量
	 * @param id
	 * @return
	 */
	public Long getCountOfSubNodeByPid(Session session,Object pidValue,String hqlSelectString){
		String hql = null;
		if(pidValue==null)
		{
			hql="select count(*) from "+tableNameString+" table_ where table_."+pidName+" is null";
		} else{
			hql="select count(*) from "+tableNameString+" table_ where table_."+pidName+"=:pv";
		}
		if(hqlSelectString!=null)
			hql+=hqlSelectString;
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("pv", pidValue);
		return this.count(session, hql,params);
	}
	
	
	/**
	 * 得到指定父节点下面的,所有节点,可以指定搜索的深度,搜索为null或者达到指定深度会自动返回数据
	 * @param session
	 * @param pidValue
	 * @param deep 搜索的深度,默认为1,最小为1;
	 * @param isContenSelf 是否包含自身
	 * @return 如果没有,返回一个长度为0的list,无序状态
	 */
	public List<T> getAllNodesByPid(Session session,Object pidValue,int deep,boolean isContenSelf,NodeType nodeType,String hqlSelectString){
		List<T> list=new ArrayList<T>();
		List<T> listResult=new ArrayList<T>();
		/**
		 * 用来保存搜索结果中的所有的父节点;
		 */
		List<Object> parentIdsList=null;	
		/**
		 * 传入搜索的父节点
		 */
		List<Object> pidsList=new ArrayList<Object>();
		pidsList.add(pidValue);
		parentIdsList=this.searchAllNodesByPid(session,pidsList, deep,isContenSelf,nodeType,list,hqlSelectString);
		if(!nodeType.equals(NodeType.All))
		{
			//开始过滤节点
			for(T t:list){
		    	try {
		    		Object parentObject;//=field.get(t);	
		    		parentObject=tree.getPid(t);
		            parentIdsList.add(parentObject);	    		
				} catch (Exception e) {
					continue;
				}	    	    	    	
		    }
			for(T t:list){
				try{
					if(nodeType.equals(NodeType.Leaf)&&!parentIdsList.contains(t))
					{
		            	listResult.add(t);
		            }else if(nodeType.equals(NodeType.Parent)&&parentIdsList.contains(t)){
		            	listResult.add(t);
		            }           
				}catch(Exception e){
					continue;
				}
			}
		}else{
			listResult.addAll(list);
		}	
		if(isContenSelf)
		{
			if(nodeType.equals(NodeType.All))
			{
				
				T t= this.getNodeById(session,pidValue,OrderType.ASC,hqlSelectString,null);
				listResult.add(t);
			}else{
			     Long count=getCountOfSubNodeByPid(session, pidValue, hqlSelectString);
			     if((nodeType.equals(NodeType.Leaf)&&count<1)||(nodeType.equals(NodeType.Parent)&&count>0)){
			    	 T t= this.getNodeById(session,pidValue,OrderType.ASC,hqlSelectString,null);
			    	 listResult.add(t);
			     }
			}
		}
		return listResult;
	}
	/**
	 * 返回指定节点搜索深度的下一层节点
	 * @param session
	 * @param pidValue
	 * @param deep
	 * @param isContenSelf
	 * @param nodeType
	 * @param list
	 * @param hqlSelectString
	 * @param parentIds
	 * @return 返回指定节点搜索深度的下一层节点的父节点列表,当NodeType为all的时候返回空列表
	 */
	private List<Object> searchAllNodesByPid(Session session,List<Object> pidValues,int deep,boolean isContenSelf, NodeType nodeType,List<T> list,String hqlSelectString){
		String hql = "";
		if(deep<1)
		{
			if(nodeType.equals(NodeType.All))
			return new ArrayList<Object>();	
			List<Object> pList=(List<Object>)this.getSubIdsByPids(session, pidValues,OrderType.ASC,hqlSelectString,null);
				return pList;
		}
		List<T> tempList=this.getSubNodeByPids(session, pidValues, OrderType.ASC, hqlSelectString, null);
		if(tempList==null||tempList.isEmpty())
	    {
	    	return new ArrayList<Object>();
	    }
	   List<Object> pids=new ArrayList<Object>();
	   list.addAll(tempList);
		for(T t:tempList){
	    	try {
	    		//Field field=cls.getDeclaredField(idName);
	    		//field.setAccessible(true);
	    		Object parentObject;//=field.get(t);	    	
	    		parentObject=tree.getPid(t);
	            pids.add(parentObject);	    				
			} catch (Exception e) {
				continue;
			}	    	    	    	
	    }
		return this.searchAllNodesByPid(session, pids, deep-1, isContenSelf, nodeType,list, hqlSelectString);
	}
    
    

	/**
	 * 
	 * @param session
	 * @param pidValue
	 * @param nodeType
	 * @param isIncludeSelf
	 * @param deep
	 * @param hqlSelectString
	 * @return
	 */
	public String getIdsByPid(Session session,Object pidValue,NodeType nodeType,boolean isIncludeSelf,int deep,String hqlSelectString){
		StringBuffer buffer=new StringBuffer();
		List<Object> list=this.getIdListByPid(session, pidValue,nodeType,isIncludeSelf, deep,hqlSelectString);
		for(int i=0;i<list.size();i++){
			if(i>0)
				buffer.append(",");
			buffer.append(list.get(i).toString());
		}
		return buffer.toString();
	}
	
	/**
	 * 得到指定父节点下面的子节点编号
	 * 通过先将所有父节点取出来,然后筛选,可以减少查询次数;
	 * @param session
	 * @param pidValue
	 * @param deep 指定搜索的深度
	 * @param isOnlyLeaf
	 * @return 
	 */
	public List<Object> getIdListByPid(Session session,Object pidValue,NodeType nodeType,boolean isIncludeSelf,int deep,String hqlSelectString){
		//保存所有子节点
		List<Object> list=new ArrayList<Object>();
		List<Object> listResult=new ArrayList<Object>();
		/**
		 * 用来保存搜索结果中的所有的父节点;
		 */
		List<Object> parentIdsList=null;	
		/**
		 * 传入搜索的父节点
		 */
		List<Object> pidsList=new ArrayList<Object>();
		pidsList.add(pidValue);
		parentIdsList=this.searchChildrenIdsByPid(session,pidsList,deep,nodeType,list,hqlSelectString);
		List<Object> parentListTempList;
		if(!nodeType.equals(NodeType.All))
		{//开始过滤节点
			parentListTempList=this.getSubPidsByPids(session, list, OrderType.ASC, hqlSelectString,null);
			parentIdsList.addAll(parentListTempList);//得到所有的父节点;
			for(Object t:list){
				try{
					if(nodeType.equals(NodeType.Leaf)&&!parentIdsList.contains(t))
					{
		            	listResult.add(t);
		            }else if(nodeType.equals(NodeType.Parent)&&parentIdsList.contains(t)){
		            	listResult.add(t);
		            }
				}catch(Exception e){
					continue;
				}
			}
			 if(nodeType.equals(NodeType.Leaf)&&list.isEmpty())
    		 {
	    	  	listResult.add(pidValue);
    		 }else if(nodeType.equals(NodeType.Parent)&&!list.isEmpty()){
	    	 listResult.add(pidValue);
    		 }
		}else{
			listResult.addAll(list);
			if(isIncludeSelf)
			{
				listResult.add(pidValue);
			}
		}	
		return listResult;
	}
	
	/**
	 * 
	 * @param session
	 * @param pidValues
	 * @param deep
	 * @param nodeType 仅仅影响返回值,NodeType.All的之后返回空的列表,其他时候返回器子节点编号列表;
	 * @param list
	 * @param hqlSelectString
	 * @return 返回的是指定节点深度的下一层节点的父节点的集合,用于判断其它节点是否是父节点,当NodeType为all的时候返回空列表;
	 */
	private List<Object> searchChildrenIdsByPid(Session session,List<Object> pidValues,int deep,NodeType nodeType,List<Object> list,String hqlSelectString){
		String hql = "";
		if(deep<1)
		{
			if(nodeType.equals(NodeType.All))
			return new ArrayList<Object>();		
			List<Object> pList=(List<Object>)this.getSubPidsByPids(session, pidValues,OrderType.ASC,hqlSelectString,null);
				return pList;
		}
		List<Object> tempList=this.getSubIdsByPids(session,pidValues,OrderType.ASC, hqlSelectString,null);
		if(tempList.isEmpty())
		{
			return tempList;
		}else{
			list.addAll(tempList);
			return this.searchChildrenIdsByPid(session,tempList, deep-1, nodeType,list, hqlSelectString);
		}
		
	}
	
    /**
     * 根据主键名称和值,取得指定节点的上级节点;
     * @param session
     * @param idName
     * @param idValue
     * @param parentName
     * @return 
     */
	public T getParent(Session session,Object idValue) {
	String hqlString=null;	
	Map<String,Object> params=new HashMap<String,Object>();
		hqlString="select p from "+tableNameString+" p,"+tableNameString+" s where s."+pidName+"=p."+idName+" and s."+idName;
	if(idValue==null){
		hqlString+="is null";	
	}else{
		hqlString+="=:idValue";
		params.put("idValue",idValue);
	}
    return this.findOne(session, hqlString, params);
	}

	/**
	 * 得到指定节点的兄弟节点,
	 * @param session
	 * @param idValue
	 * @param orderType
	 * @param isIncludeSelf 是否包含自己
	 * @param sortColumns
	 * @return
	 */
	public List<T> getTreeBrothersNode(Session session,Object idValue,boolean isIncludeSelf,OrderType orderType,String...sortColumns) {
		List<T> list=null;
		String hqlString=null;	
		Map<String,Object> params=new HashMap<String,Object>();
			hqlString="select brothers from "+tableNameString+" brothers where brothers."+idName;
		if(idValue==null){
			hqlString+="is not null";	
		}else{
			hqlString+="<>:idValue";						
		}
		if(!isIncludeSelf){
			hqlString+=" and brothers."+pidName+" in ";
			hqlString+="(select p."+pidName+" from tableNameString p,"+tableNameString+" s where s."+pidName+"=p."+idName+" and s."+idName;
		}
		if(idValue==null){
			hqlString+="is null";
		}else{
			hqlString+="=:idValue";
			params.put("idValue",idValue);
		}
		hqlString+=")";
		params.put("idValue",idValue);
		hqlString+=getOrderSortHqlString(orderType, sortColumns);
		return (List<T>) this.findList(session, hqlString, params);
	}
	
	/**
	 * List参数会自动转换,不要传入数组;
	 * @param session
	 * @param hql
	 * @param params
	 * @return
	 */
	private List<?> findList(Session session,String hql, Map<String, Object> params) {
		Query q =session.createQuery(hql);
		this.setParameterToQuery(q, params);
		return q.list();
	}
	private Long count(Session session,String hql,Map<String, Object> params) {
		Query q = session.createQuery(hql);
		this.setParameterToQuery(q, params);
		return (Long) q.uniqueResult();
	}
	private T findOne(Session session,String hql, Map<String, Object> params) {
		Query q =session.createQuery(hql);
		this.setParameterToQuery(q, params);
		return (T) q.uniqueResult();
	}
	
	/**
	 * @param q
	 * @param params 当前支持普通对象,数组,集合三种类型的参数
	 */
	protected void setParameterToQuery(Query q,Map<String, Object> params){
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				if(params.get(key) instanceof Object[]){
					Object[] objs=(Object[]) params.get(key);
					q.setParameterList(key, objs);
				}else if(params.get(key) instanceof Collection<?>){
					Collection<?> collection=(Collection<?>) params.get(key);
					q.setParameterList(key, collection);
				}else{
					q.setParameter(key, params.get(key));
				}
			}
		}
	}
}
