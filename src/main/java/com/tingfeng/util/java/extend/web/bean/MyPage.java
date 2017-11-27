package com.tingfeng.util.java.extend.web.bean;
/**
 * 
 * @author Administrator
 * 分页的基本信息,主要用来接收前台数据
 */
public class MyPage {
	/**
	 * 每页的记录条数,默认20
	 */
private Integer rows=20;
/**
 * 当前的页数,默认1
 */
private Integer page=1;
/**
 * 总的记录数量
 */
private Long total;
/**
 * 想要排序的方式,有'desc'和'asc'两种
 */
private String order;
/**
 * 排序的字段
 */
private String sort;
 
	public MyPage() {
		// TODO Auto-generated constructor stub
	}
	public MyPage(Integer rows, Integer page) {
		super();
		this.rows = rows;
		this.page = page;
	}
	
	public MyPage(Integer rows, Integer page,String order,
			String sort) {
		super();
		this.rows = rows;
		this.page = page;
		this.order = order;
		this.sort = sort;
	}
	
	public Integer getRows() {
		if(rows==null)
			rows=20;
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	public Integer getPage() {
		if(page==null)
			page=1;
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public String getOrder() {
		if(order==null)
			return null;
		if(!order.equals("desc"))
		return "asc";
		return "desc";
	}
	/**
	 * 由于目前采用的拼串的方式来进行的排序,为了方式sql注入,所以对其进行一定的处理,即如果sort中含有空格,那么返回null
	 * @return
	 */
	public String getSort() {
		if(sort!=null)
		{sort=sort.trim();
		if(sort.indexOf(' ')>=0)
			return null;}
		return sort;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}

}
