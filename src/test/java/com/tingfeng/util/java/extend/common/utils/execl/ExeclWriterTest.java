package com.tingfeng.util.java.extend.common.utils.execl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class ExeclWriterTest {
	
	/** 
     * 测试方法 
	 * @throws IOException 
     */  
	public static void main(String[] args) throws IOException {
	   new ExeclWriterTest().test();
    }  
	@Test
	public void test()  throws IOException {
		 String path = "E:/test.xlsx";	    
		    File file = new File("E:/dest.xlsx");
		    if(!file.exists()) {
		    	file.createNewFile();
		    }
		    
		    FileOutputStream destFile = new FileOutputStream(file);	   
		    ExeclWriterI writer = new ExcelWriterHelperBySXSSF(path,destFile);
		    writer.startWriteData("sheet");
		    for (int rownum = 0; rownum < 500000; rownum++) {
		    	List<Object> dataList = new ArrayList<Object>();
		    	dataList.add("_ " + rownum);
		    	dataList.add(34343.123456789d);  
		    	dataList.add("23.67%");  
		    	dataList.add("12:12:23");  
		    	dataList.add("2014-10-11 12:12:23");  
		    	dataList.add("true");
		    	dataList.add(false);
				dataList.add(new Date());
		    	writer.writeData(dataList, rownum);   
		    }
		    writer.endWriteData();
	}

}
