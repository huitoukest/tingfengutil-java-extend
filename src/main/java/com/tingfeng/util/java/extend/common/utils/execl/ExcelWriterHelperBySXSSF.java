package com.tingfeng.util.java.extend.common.utils.execl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



/**
 * 此为execl的write的默认实现ByXSSFWorkbook.
 * 支持Excel 2007 OOXML (.xlsx)
 * @author huitoukest
 *
 */
public class ExcelWriterHelperBySXSSF implements ExeclWriterI{
    /**
     * 内存中驻留的数据行值
     */
	public  int rowAccessWindowSize = 200;

	private File destFile;
    private SXSSFWorkbook sxssfWorkbook=null;
    private SXSSFSheet sxssfSheet=null;
	
    public ExcelWriterHelperBySXSSF(String filePath){
        this(new File(filePath));
    }
    
	public ExcelWriterHelperBySXSSF(File destFile){
        this.destFile=destFile;
    }

	/**
	 * SXSSF是xml方式写入Execl文件.所以需要对XML中一些元素做转意处理
	 * @param string
	 * @return
	 */
	public static String getEncoderXMLString(String string) {  
        string=StringUtils.replace(string,"&","&amp;");
        string=StringUtils.replace(string,"\"","&quot;");
        string=StringUtils.replace(string,"<","&lt;");
        string=StringUtils.replace(string,">","&gt;");
        string=StringUtils.replace(string,"\'","&#39;");
        return string;
    }
	
	@Override
	public void startWriteData(String sheetName) throws IOException {
		    if(StringUtils.isBlank(sheetName)){
	            sheetName = "sheet1";
	        }        
	        FileInputStream fs=new FileInputStream(destFile);                
	        // 建立工作簿和电子表格对象  
	        XSSFWorkbook xssfWorkbook= new XSSFWorkbook(fs); // 加载excel的 工作目录  
	        sxssfWorkbook=new SXSSFWorkbook(xssfWorkbook,rowAccessWindowSize);

	        sxssfSheet= sxssfWorkbook.getSheet(sheetName);// 获取一个工作薄对象
	        if (sxssfSheet == null) {
	            sxssfSheet=sxssfWorkbook.createSheet(sheetName);// 如果没有这个Sheet 创建一个
	        }
	        
	        if(null!=sxssfWorkbook.getSheet("deleteSheet1"))
	        {//删除特定的sheet
	            sxssfWorkbook.removeSheetAt(sxssfWorkbook.getSheetIndex("deleteSheet1"));      
	        }   	
	}
	
	@Override
	public void writeData(List<?> dataList, int startRowNumber) throws IOException {           
		this.insertDataToExeclRow(this.sxssfSheet,startRowNumber,dataList);	                           
	}
	/**
     * 正常运行后会自动掉头用deleteTempFile()删除临时文件。
     * @throws IOException
     */
	@Override
	public void endWriteData() throws IOException {
	        FileOutputStream out = new FileOutputStream(destFile);  
	        sxssfWorkbook.write(out);
	        out.close(); 
	        sxssfWorkbook.dispose();
	        System.gc();	        	        
	}
	
	/**
	 * 删除临时文件
	 */
	/*@Override
	public void deleteTempFile() {
		
	}*/

	@Override
	public void insertDataToExeclRow(int rowNumber,List<?> rowData) throws IOException {
		this.insertDataToExeclRow(this.sxssfSheet, rowNumber, rowData);
	}
	
	/**
	 * 支持常见的Number,Date,Calendar,String类型,其余会自动调用toString(),null转为""
	 * @param sxssfSheet
	 * @param rowNumber
	 * @param rowData
	 * @throws IOException
	 */
	public void insertDataToExeclRow(SXSSFSheet sxssfSheet,int rowNumber,List<?> rowData) throws IOException{
	    SXSSFRow sxssRow = sxssfSheet.createRow(rowNumber);
	    for(int j = 0 ; j < rowData.size() ; j++ ){	      
	    	Object data = rowData.get(j);
	    	SXSSFCell cell = sxssRow.createCell(j);
	    	insertDataToExeclCell(data,cell);
	    }
	}
	
	private void insertDataToExeclCell(Object data,SXSSFCell cell ) {
    	if(null == data) {
    		cell.setCellValue("");
    	}else if(data instanceof String) {
    		cell.setCellValue((String)data);
    	}else if(data instanceof Date) {
	    	cell.setCellValue((Date)data);
	    }else if(data instanceof Calendar) {
	    	cell.setCellValue((Calendar)data);
	    }else {
    		String valueString = data.toString();
    		if(data instanceof Number) {
	    		if(valueString.indexOf(".") > 0 ) {
	    			Double value = Double.valueOf(valueString);
	    			cell.setCellValue(value);
	    		}else {
	    			Long value = Long.valueOf(valueString);
	    	    	cell.setCellValue(value);
	    		}	
    		}else {
    			cell.setCellValue(valueString);
    		}
	    }
	}
	
	/**
	 * 插入一列数据
	 * @param rowNumber
	 * @param columnNumber
	 */
	public void insertDataToExeclCell(int rowNumber,int columnNumber,Object data) {
		SXSSFRow sxssRow = sxssfSheet.createRow(rowNumber);
		SXSSFCell cell = sxssRow.createCell(columnNumber);
		insertDataToExeclCell(data,cell);
	}

}
